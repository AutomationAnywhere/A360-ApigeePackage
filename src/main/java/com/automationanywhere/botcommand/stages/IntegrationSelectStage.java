/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.stages;

import static com.automationanywhere.commandsdk.model.AttributeType.RECORDPICKER;

import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;

@BotCommand(commandType = BotCommand.CommandType.DESKTOP_OPERATION_STAGE)
@CommandPkg(
        name = "integrationSelectStage",
        desktop_operation_name = "integrationDiscovery",
        label = "[[Discovery.select.stage.label]]",
        submit_label = "[[Discovery.select.stage.submit.label]]",
        remember = true)
public class IntegrationSelectStage {
    @Idx(index = "1", type = RECORDPICKER)
    @Pkg(
            label = "[[Discovery.select.stage.integrationName.label]]",
            desktop_operation_name = "integrationTableOperation",
            desktop_operation_attributes = {"integrationTable"})
    @NotEmpty
    String integration;
}
