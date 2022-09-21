/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.operations;

import static java.util.Collections.emptyMap;
import static org.apache.logging.log4j.LogManager.getLogger;

import com.automationanywhere.botcommand.constants.CommandMessages;
import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.*;
import com.automationanywhere.botcommand.data.model.record.Record;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.botcommand.exceptions.HttpClientException;
import com.automationanywhere.botcommand.objects.AuthenticationContext;
import com.automationanywhere.botcommand.objects.apigee.Integration;
import com.automationanywhere.botcommand.objects.apigee.IntegrationParameter;
import com.automationanywhere.botcommand.services.AuthenticationService;
import com.automationanywhere.botcommand.services.IntegrationService;
import com.automationanywhere.botcommand.utilities.JsonSerializer;
import com.automationanywhere.botcommand.utilities.LabelHelper;
import com.automationanywhere.botcommand.utilities.PageUtils;
import com.automationanywhere.botcommand.utilities.TableUtils;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.desktopoperations.OnStage;
import com.automationanywhere.commandsdk.annotations.desktopoperations.Request;
import com.automationanywhere.core.security.SecureString;
import com.automationanywhere.toolchain.runtime.operation.DesktopOperationButtonRequest;
import com.automationanywhere.toolchain.runtime.operation.DesktopOperationButtonResponse;
import com.automationanywhere.toolchain.runtime.operation.DesktopOperationDataItem;
import com.automationanywhere.toolchain.runtime.operation.DesktopOperationStage;
import com.automationanywhere.toolchain.runtime.operation.DesktopOperationStage.StateItem;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;

@BotCommand(commandType = BotCommand.CommandType.DESKTOP_OPERATION_BUTTON)
@CommandPkg(name = "integrationDiscovery", desktop_operation_name = "IntegrationDiscovery")
public class IntegrationDiscovery {
    private final String[] locations =
            new String[] {
                "us",
                "northamerica-northeast1",
                "europe-west2",
                "asia-southeast1",
                "asia-south1",
                "australia-southeast1",
                "us-central1",
                "us-east1",
                "us-west1",
                "europe-west1",
                "europe-west3",
                "asia-east1",
                "southamerica-east1",
                "us-east4",
                "us-west2",
                "asia-northeast1",
                "europe-west4",
                "europe-west6"
            };
    private static final Logger logger = getLogger();
    private static final String CONNECTION_STAGE = "connectionStage";
    private static final String CONFIGURATION_STAGE = "configurationStage";
    private static final String SELECT_STAGE = "integrationSelectStage";
    private static final String INTEGRATION_CONFIGURATION_STAGE = "integrationConfigurationStage";

    private AuthenticationService _authenticationService;
    private IntegrationService _integrationService;
    private AuthenticationContext authenticationContext;

    public IntegrationDiscovery() {}

    public IntegrationDiscovery(
            AuthenticationService authenticationService, IntegrationService integrationService) {
        this._authenticationService = authenticationService;
        this._integrationService = integrationService;
    }

    private AuthenticationService getAuthenticationService() {
        if (_authenticationService != null) {
            return _authenticationService;
        }
        return new AuthenticationService(null);
    }

    private IntegrationService getIntegrationService() {
        if (_integrationService != null) {
            return _integrationService;
        }
        return new IntegrationService(authenticationContext, null);
    }

    @Request private DesktopOperationButtonRequest request;

    public void setRequest(DesktopOperationButtonRequest request) {
        this.request = request;
    }

    @OnStage(name = "")
    public DesktopOperationButtonResponse getInitialStageData() {
        logger.info("Desktop operation - load initial stage");
        logger.info("Desktop operation - return desktopOperationButtonResponse for initial stage");
        return new DesktopOperationButtonResponse(
                emptyMap(), emptyMap(), new DesktopOperationStage(CONNECTION_STAGE, emptyMap()));
    }

    @OnStage(name = CONNECTION_STAGE)
    public DesktopOperationButtonResponse getConnectionStageData() throws IOException {
        logger.info("Desktop operation - request called for stage connectionStage");

        var clientId = (String) request.getAttributes().get("clientId").get();
        var clientSecret = (String) request.getAttributes().get("clientSecret").get();
        var userEmailAddress = (String) request.getAttributes().get("userEmailAddress").get();
        var redirectUri = (String) request.getAttributes().get("redirectUri").get();

        var secureClientId = new SecureString(clientId.getBytes());
        var secureClientSecret = new SecureString(clientSecret.getBytes());
        var secureUserEmailAddress = new SecureString(userEmailAddress.getBytes());
        var secureRedirectUri = new SecureString(redirectUri.getBytes());

        authenticationContext =
                new AuthenticationContext(
                        secureUserEmailAddress,
                        secureClientId,
                        secureClientSecret,
                        secureRedirectUri);
        try {
            var service = getAuthenticationService();
            service.authenticate(authenticationContext);
        } catch (HttpClientException ex) {
            throw new BotCommandException(ex.getMessage());
        }

        Map<String, StateItem> stageDataMap = getStageDataMap(authenticationContext);

        logger.info(
                "Desktop operation - return desktopOperationButtonResponse for connectionStage");
        return new DesktopOperationButtonResponse(
                emptyMap(),
                emptyMap(),
                new DesktopOperationStage(CONFIGURATION_STAGE, stageDataMap));
    }

    @OnStage(name = CONFIGURATION_STAGE)
    public DesktopOperationButtonResponse getIntegrationData() throws IOException {
        logger.info("Desktop operation - request called for stage configurationStage");
        var projectId = (String) request.getAttributes().get("projectId").get();
        var location = (String) request.getAttributes().get("location").get();

        var stateMap = request.getStage().getState();
        stateMap.put("projectId", new StateItem(projectId));
        stateMap.put("location", new StateItem(location));

        authenticationContext =
                JsonSerializer.deserialize(
                        stateMap.get("authenticationContext").getValue(),
                        AuthenticationContext.class);

        var integrationService = getIntegrationService();
        var integrations = new ArrayList<Integration>();
        if (location.equals("all")) {
            for (String l : locations) {
                integrations.addAll(integrationService.listIntegrations(projectId, l));
            }
        } else {
            integrations.addAll(integrationService.listIntegrations(projectId, location));
        }
        if (integrations.stream().count() == 0) {
            if (location != "all") {
                for (String l : locations) {
                    integrations.addAll(integrationService.listIntegrations(projectId, l));
                }
            }
            if (integrations.stream().count() == 0) {
                throw new BotCommandException(
                        LabelHelper.getString(CommandMessages.ERROR_NO_INTEGRATIONS_FOUND));
            }
        }

        var attributeDataMap = getIntegrationsAttributeDataMap(integrations);
        logger.info(
                "Desktop operation - return desktopOperationButtonResponse for configurationStage");
        return new DesktopOperationButtonResponse(
                emptyMap(), attributeDataMap, new DesktopOperationStage(SELECT_STAGE, stateMap));
    }

    @OnStage(name = SELECT_STAGE)
    public DesktopOperationButtonResponse getIntegrationSelectionData() throws IOException {
        logger.info("Desktop operation - request called for stage integrationSelectStage");
        var integrationRecord = getIntegrationRecord();

        var integrationNameStringValue = (StringValue) integrationRecord.getValues().get(0);
        var locationStringValue = (StringValue) integrationRecord.getValues().get(2);
        var integrationName = integrationNameStringValue.get();
        var location = locationStringValue.get();
        logger.info("Desktop operation - selected integration name: " + integrationName);

        var stateMap = request.getStage().getState();
        stateMap.put("integrationName", new StateItem(integrationName));
        stateMap.put("location", new StateItem(location));

        var projectId = stateMap.get("projectId").getValue();

        authenticationContext =
                JsonSerializer.deserialize(
                        stateMap.get("authenticationContext").getValue(),
                        AuthenticationContext.class);

        var integrationService = getIntegrationService();
        var inputParams =
                integrationService.getIntegrationInputParameters(
                        projectId, location, integrationName);

        var attributeDataMap = getParametersAttributeDataMap(inputParams);
        stateMap.put("inputParameters", new StateItem(JsonSerializer.serialize(inputParams)));

        logger.info(
                "Desktop operation - return desktopOperationButtonResponse for integrationSelectStage");
        return new DesktopOperationButtonResponse(
                emptyMap(),
                attributeDataMap,
                new DesktopOperationStage(INTEGRATION_CONFIGURATION_STAGE, stateMap));
    }

    private Record getIntegrationRecord() {
        var integrationListValue = (ListValue) request.getAttributes().get("integration");
        var integrationList = integrationListValue.get();
        var integrationRecordValue = (RecordValue) integrationList.stream().findFirst().get();
        var integrationRecord = integrationRecordValue.get();
        return integrationRecord;
    }

    private static ListValue convertParametersToEntryList(List<IntegrationParameter> parameters) {
        var parameterList = new ArrayList<DictionaryValue>();
        for (var parameter : parameters) {
            var dictValue = new DictionaryValue();
            var map = new HashMap<String, Value>();
            var defaultValue =
                    parameter.defaultValue != null ? parameter.defaultValue.toString() : "";
            map.put("INPUT_PARAMETER_NAME", new StringValue(parameter.key));
            map.put("INPUT_PARAMETER_TYPE", new StringValue(parameter.datatypeDisplayString()));
            map.put("INPUT_PARAMETER_VALUE", new StringValue(defaultValue));
            dictValue.set(map);
            parameterList.add(dictValue);
        }
        ListValue entryList = new ListValue<DictionaryValue>();
        entryList.set(parameterList);
        return entryList;
    }

    @OnStage(name = INTEGRATION_CONFIGURATION_STAGE)
    public DesktopOperationButtonResponse getParamData() {
        logger.info("Desktop operation - request called for stage integrationConfigurationStage");
        HashMap<String, Value> valueMap = getAttributeMap();
        logger.info(
                "Desktop operation - return desktopOperationButtonResponse for integrationConfigurationStage");
        return new DesktopOperationButtonResponse(valueMap, emptyMap(), null);
    }

    private HashMap<String, Value> getAttributeMap() {
        var projectId = new StringValue(request.getStage().getState().get("projectId").getValue());
        var location = new StringValue(request.getStage().getState().get("location").getValue());
        Type listOfIntegrationParameters =
                new TypeToken<ArrayList<IntegrationParameter>>() {}.getType();
        List<IntegrationParameter> inputParameters =
                JsonSerializer.deserialize(
                        request.getStage().getState().get("inputParameters").getValue(),
                        listOfIntegrationParameters);
        var integrationName =
                new StringValue(request.getStage().getState().get("integrationName").getValue());
        var triggerId = (StringValue) request.getAttributes().get("triggerId");

        var valueMap = new HashMap<String, Value>();
        valueMap.put("projectId", projectId);
        valueMap.put("location", location);
        valueMap.put("integrationName", integrationName);
        valueMap.put("triggerId", triggerId);
        if (inputParameters != null) {
            var entryList = convertParametersToEntryList(inputParameters);
            valueMap.put("inputParameters", entryList);
        }
        return valueMap;
    }

    private Map<String, StateItem> getStageDataMap(AuthenticationContext authenticationContext) {
        Map<String, StateItem> stageData = new HashMap<>();
        stageData.put(
                "authenticationContext",
                new StateItem(JsonSerializer.serialize(authenticationContext)));

        return stageData;
    }

    private Map<String, DesktopOperationDataItem> getIntegrationsAttributeDataMap(
            List<Integration> integrations) {
        TableValue integrationTable = TableUtils.convertIntegrationsToTable(integrations);
        PageUtils.Page<Row> tablePage = PageUtils.paginate(integrationTable.get().getRows());

        Map<String, DesktopOperationDataItem> data = new HashMap<>();
        data.put(
                "integration",
                new DesktopOperationDataItem.DataItemTable(
                        integrationTable, tablePage.getPagination()));

        return data;
    }

    private Map<String, DesktopOperationDataItem> getParametersAttributeDataMap(
            List<IntegrationParameter> parameters) {
        TableValue integrationTable = TableUtils.convertParametersToTable(parameters);
        PageUtils.Page<Row> tablePage = PageUtils.paginate(integrationTable.get().getRows());

        Map<String, DesktopOperationDataItem> data = new HashMap<>();
        data.put(
                "inputParameters",
                new DesktopOperationDataItem.DataItemTable(
                        integrationTable, tablePage.getPagination()));

        return data;
    }
}
