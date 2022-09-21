/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.stages;

import static com.automationanywhere.commandsdk.model.AttributeType.CREDENTIAL;

import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;

@BotCommand(commandType = BotCommand.CommandType.DESKTOP_OPERATION_STAGE)
@CommandPkg(
        name = "connectionStage",
        desktop_operation_name = "integrationDiscovery",
        label = "[[Discovery.connect.stage.label]]",
        submit_label = "[[Discovery.connect.stage.submit.label]]",
        remember = true)
public class ConnectionStage {
    @Idx(index = "1", type = CREDENTIAL)
    @Pkg(label = "[[Discovery.connect.stage.userEmailAddress.label]]")
    @NotEmpty
    String userEmailAddress;

    @Idx(index = "2", type = CREDENTIAL)
    @Pkg(label = "[[Discovery.connect.stage.clientId.label]]")
    @NotEmpty
    String clientId;

    @Idx(index = "3", type = CREDENTIAL)
    @Pkg(label = "[[Discovery.connect.stage.clientSecret.label]]")
    @NotEmpty
    String clientSecret;

    @Idx(index = "4", type = CREDENTIAL)
    @Pkg(label = "[[Discovery.connect.stage.redirectUri.label]]")
    @NotEmpty
    String redirectUri;
}
