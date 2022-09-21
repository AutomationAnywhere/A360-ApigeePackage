/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.operations.select;

import static org.apache.logging.log4j.LogManager.getLogger;

import com.automationanywhere.botcommand.objects.AuthenticationContext;
import com.automationanywhere.botcommand.services.OrganizationService;
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
@CommandPkg(name = "GetProjectIds", desktop_operation_name = "IntegrationDiscovery")
public class GetProjectIds {
    private static final Logger logger = getLogger();

    @Request private DesktopOperationSelectRequest request;

    public void setRequest(DesktopOperationSelectRequest request) {
        this.request = request;
    }

    private static final String CONFIGURATION_STAGE = "configurationStage";

    @OnStage(name = CONFIGURATION_STAGE)
    public DesktopOperationSelectResponse execute() throws IOException {
        logger.info("Desktop operation - begin project select response");
        Map<String, DesktopOperationStage.StateItem> stateMap = request.getStage().getState();

        AuthenticationContext authenticationContext =
                JsonSerializer.deserialize(
                        stateMap.get("authenticationContext").getValue(),
                        AuthenticationContext.class);

        var organizationService = new OrganizationService(authenticationContext, null);
        var projectIds = organizationService.listProjectIds();

        List<DesktopOperationSelectItem> projects = new ArrayList<>();
        for (var project : projectIds) {
            projects.add(new DesktopOperationSelectItem(project, project));
        }
        return new DesktopOperationSelectResponse(projects, null);
    }
}
