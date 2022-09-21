/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.services;

import static com.automationanywhere.botcommand.constants.Endpoints.*;

import com.automationanywhere.bot.service.GlobalSessionContext;
import com.automationanywhere.botcommand.constants.CommandMessages;
import com.automationanywhere.botcommand.constants.Endpoints;
import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.objects.AuthenticationContext;
import com.automationanywhere.botcommand.objects.apigee.*;
import com.automationanywhere.botcommand.objects.apigee.enums.InputOutputType;
import com.automationanywhere.botcommand.services.abstracts.AuthenticatedBaseService;
import com.automationanywhere.botcommand.utilities.HttpClient;
import com.automationanywhere.botcommand.utilities.IntegrationParameterConverter;
import com.automationanywhere.botcommand.utilities.JsonSerializer;
import com.automationanywhere.botcommand.utilities.StringUtility;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

public class IntegrationService extends AuthenticatedBaseService {
    private final String API_TRIGGER_TYPE = "API";

    public IntegrationService(
            AuthenticationContext authenticationContext,
            GlobalSessionContext globalSessionContext) {
        super(authenticationContext, globalSessionContext);
    }

    public IntegrationService(AuthenticationService authenticationService, HttpClient client) {
        super(authenticationService, client);
    }

    public List<Integration> listIntegrations(String projectId, String location)
            throws IOException {
        ValidateProjectIdAndLocation(projectId, location);

        var listPath = String.format(LIST_INTEGRATIONS_ENDPOINT, projectId, location);
        var url = String.format(INTEGRATIONS_API_BASE_URL, location) + listPath;

        var response = sendRequest(url, Method.GET, IntegrationsListResponse.class);
        if (response != null && response.integrations != null) {
            response.integrations.forEach(i -> i.location = location);
            Collections.sort(
                    response.integrations,
                    Comparator.comparing(i -> i.name, String.CASE_INSENSITIVE_ORDER));
            return response.integrations;
        }
        return Collections.emptyList();
    }

    private IntegrationVersionsListResponse listIntegrationVersions(
            String projectId, String location, String integrationName) throws IOException {
        ValidateProjectIdAndLocation(projectId, location);

        if (StringUtility.isNullOrEmpty(integrationName)) {
            throw new InvalidParameterException(CommandMessages.ERROR_INVALID_INTEGRATION_NAME);
        }

        var listPath =
                String.format(
                        LIST_INTEGRATION_VERSIONS_ENDPOINT, projectId, location, integrationName);
        var url = String.format(INTEGRATIONS_API_BASE_URL, location) + listPath;

        return sendRequest(url, Endpoints.Method.GET, IntegrationVersionsListResponse.class);
    }

    public List<IntegrationParameter> getIntegrationInputParameters(
            String projectId, String location, String integrationName) throws IOException {
        var latestVersion = getCurrentIntegrationVersion(projectId, location, integrationName);
        final var inputTypes =
                Arrays.asList(InputOutputType.IN.toString(), InputOutputType.IN_OUT.toString());
        if (latestVersion.integrationParameters != null) {
            return latestVersion.integrationParameters.stream()
                    .filter(p -> inputTypes.contains(p.inputOutputType))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<IntegrationTriggerConfig> getApiTriggers(
            String projectId, String location, String integrationName) throws IOException {
        var latestVersion = getCurrentIntegrationVersion(projectId, location, integrationName);
        return latestVersion.triggerConfigs.stream()
                .filter(c -> Objects.equals(c.triggerType, API_TRIGGER_TYPE))
                .collect(Collectors.toList());
    }

    private IntegrationVersion getCurrentIntegrationVersion(
            String projectId, String location, String integrationName) throws IOException {
        var listResponse = listIntegrationVersions(projectId, location, integrationName);

        // the latest version is always listed first, the status can be either active or draft
        var latestVersion = listResponse.integrationVersions.stream().findFirst().get();
        return latestVersion;
    }

    private void ValidateProjectIdAndLocation(String projectId, String location) {
        if (StringUtility.isNullOrEmpty(projectId)) {
            throw new InvalidParameterException(CommandMessages.ERROR_INVALID_PROJECT_ID);
        }

        if (StringUtility.isNullOrEmpty(location)) {
            throw new InvalidParameterException(CommandMessages.ERROR_INVALID_LOCATION);
        }
    }

    public Map<String, Object> executeIntegration(
            String projectId,
            String location,
            String integrationName,
            String triggerId,
            Map<String, Value> inputParameters)
            throws IOException {
        ValidateProjectIdAndLocation(projectId, location);

        if (StringUtility.isNullOrEmpty(integrationName)) {
            throw new InvalidParameterException(CommandMessages.ERROR_INVALID_INTEGRATION_NAME);
        }

        if (StringUtility.isNullOrEmpty(triggerId)) {
            throw new InvalidParameterException(CommandMessages.ERROR_INVALID_TRIGGER_ID);
        }

        var integrationParameters =
                getIntegrationInputParameters(projectId, location, integrationName);
        var convertedInputParameters =
                IntegrationParameterConverter.convertInputParameters(
                        inputParameters, integrationParameters);

        final var executePath =
                String.format(EXECUTE_INTEGRATIONS_ENDPOINT, projectId, location, integrationName);
        var executeIntegrationRequest =
                new ExecuteIntegrationRequest(executePath, triggerId, convertedInputParameters);
        var url = String.format(INTEGRATIONS_API_BASE_URL, location) + executePath;
        return sendRequest(
                        url,
                        Endpoints.Method.POST,
                        JsonSerializer.serialize(executeIntegrationRequest),
                        ExecuteIntegrationResponse.class)
                .outputParameters;
    }
}
