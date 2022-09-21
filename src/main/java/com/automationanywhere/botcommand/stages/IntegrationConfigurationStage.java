/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.stages;

import static com.automationanywhere.commandsdk.model.AttributeType.DESKTOPOPERATIONSELECT;
import static com.automationanywhere.commandsdk.model.AttributeType.RECORDPICKER;

import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;

@BotCommand(commandType = BotCommand.CommandType.DESKTOP_OPERATION_STAGE)
@CommandPkg(
        name = "integrationConfigurationStage",
        desktop_operation_name = "integrationDiscovery",
        label = "[[Discovery.params.stage.label]]",
        submit_label = "[[Discovery.params.stage.submit.label]]",
        remember = true)
public class IntegrationConfigurationStage {
    @Idx(index = "1", type = DESKTOPOPERATIONSELECT)
    @Pkg(
            label = "[[Discovery.params.stage.apiTrigger.label]]",
            desktop_operation_name = "GetApiTriggers")
    @NotEmpty
    String triggerId;

    @Idx(index = "2", type = RECORDPICKER)
    @Pkg(
            label = "[[ExecuteIntegration.entryList.label]]",
            description = "[[ExecuteIntegration.entryList.description]]",
            desktop_operation_name = "inputParameterTableOperation",
            desktop_operation_attributes = {"inputParameterTable"})
    String inputParameters;
}
