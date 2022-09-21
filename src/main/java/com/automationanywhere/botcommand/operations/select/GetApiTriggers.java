/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.operations.select;

import static org.apache.logging.log4j.LogManager.getLogger;

import com.automationanywhere.botcommand.objects.AuthenticationContext;
import com.automationanywhere.botcommand.services.IntegrationService;
import com.automationanywhere.botcommand.utilities.JsonSerializer;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.desktopoperations.OnStage;
import com.automationanywhere.commandsdk.annotations.desktopoperations.Request;
import com.automationanywhere.toolchain.runtime.operation.DesktopOperationSelectItem;
import com.automationanywhere.toolchain.runtime.operation.DesktopOperationSelectRequest;
import com.automationanywhere.toolchain.runtime.operation.DesktopOperationSelectResponse;
import com.automationanywhere.toolchain.runtime.operation.DesktopOperationStage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;

@BotCommand(commandType = BotCommand.CommandType.DESKTOP_OPERATION_SELECT)
@CommandPkg(name = "GetApiTriggers", desktop_operation_name = "IntegrationDiscovery")
public class GetApiTriggers {
    private static final Logger logger = getLogger();

    @Request private DesktopOperationSelectRequest request;

    private AuthenticationContext authenticationContext;
    private IntegrationService _integrationService;

    public GetApiTriggers() {}

    public GetApiTriggers(IntegrationService integrationService) {
        this._integrationService = integrationService;
    }

    public void setRequest(DesktopOperationSelectRequest request) {
        this.request = request;
    }

    private static final String INTEGRATION_CONFIGURATION_STAGE = "integrationConfigurationStage";

    @OnStage(name = INTEGRATION_CONFIGURATION_STAGE)
    public DesktopOperationSelectResponse execute() throws IOException {
        logger.info("Desktop operation - begin api trigger select response");
        Map<String, DesktopOperationStage.StateItem> stateMap = request.getStage().getState();

        authenticationContext =
                JsonSerializer.deserialize(
                        stateMap.get("authenticationContext").getValue(),
                        AuthenticationContext.class);

        var projectId = stateMap.get("projectId").getValue();
        var location = stateMap.get("location").getValue();
        var integrationName = stateMap.get("integrationName").getValue();

        var apiTriggers =
                getIntegrationService().getApiTriggers(projectId, location, integrationName);

        List<DesktopOperationSelectItem> triggers = new ArrayList<>();
        for (var trigger : apiTriggers) {
            triggers.add(new DesktopOperationSelectItem(trigger.label, trigger.triggerId));
        }
        return new DesktopOperationSelectResponse(triggers, null);
    }

    private IntegrationService getIntegrationService() {
        if (_integrationService != null) {
            return _integrationService;
        }
        return new IntegrationService(authenticationContext, null);
    }
}
