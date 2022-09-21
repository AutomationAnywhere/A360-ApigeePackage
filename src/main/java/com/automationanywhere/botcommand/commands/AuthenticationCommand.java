/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.commands;

import static com.automationanywhere.commandsdk.model.AttributeType.CREDENTIAL;

import com.automationanywhere.botcommand.constants.CommandMessages;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.botcommand.objects.AuthenticationContext;
import com.automationanywhere.botcommand.services.AuthenticationService;
import com.automationanywhere.botcommand.utilities.LabelHelper;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import com.automationanywhere.core.security.SecureString;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@BotCommand
@CommandPkg(
        label = "[[Connect.label]]",
        description = "[[Connect.description]]",
        name = "Connect",
        node_label = "",
        icon = "apigee.svg")
public class AuthenticationCommand extends BaseCommand<AuthenticationService> {
    @Sessions private Map<String, Object> sessionMap;

    @GlobalSessionContext
    private com.automationanywhere.bot.service.GlobalSessionContext globalSessionContext;

    public void setGlobalSessionContext(
            com.automationanywhere.bot.service.GlobalSessionContext globalSessionContext) {
        this.globalSessionContext = globalSessionContext;
    }

    @Execute
    public void execute(
            @Idx(index = "1", type = CREDENTIAL)
                    @Pkg(label = "[[Connect.userEmailAddress.label]]")
                    @NotEmpty
                    SecureString userEmailAddress,
            @Idx(index = "2", type = CREDENTIAL)
                    @Pkg(
                            label = "[[Connect.clientId.label]]",
                            description = "[[Connect.clientId.description]]")
                    @NotEmpty
                    SecureString clientId,
            @Idx(index = "3", type = CREDENTIAL)
                    @Pkg(
                            label = "[[Connect.clientSecret.label]]",
                            description = "[[Connect.clientSecret.description]]",
                            default_value_type = DataType.STRING)
                    @NotEmpty
                    SecureString clientSecret,
            @Idx(index = "4", type = CREDENTIAL)
                    @Pkg(
                            label = "[[Connect.redirectURI.label]]",
                            description = "[[Connect.redirectURI.description]]",
                            default_value_type = DataType.STRING)
                    @NotEmpty
                    SecureString redirectUri,
            @Idx(index = "5", type = AttributeType.TEXT)
                    @Pkg(
                            label = "[[Connect.session.label]]",
                            description = "[[Connect.session.description]]",
                            default_value = "Default",
                            default_value_type = DataType.STRING)
                    @NotEmpty
                    String session) {
        try {
            this.setSession(this.sessionMap, session, globalSessionContext);
            if (this.getAuthenticationService() == null) {
                var authenticationService = new AuthenticationService(globalSessionContext);
                this.setAuthenticationService(authenticationService);
            }

            var authenticationContext =
                    new AuthenticationContext(
                            userEmailAddress, clientId, clientSecret, redirectUri);
            this.setAuthenticationContext(authenticationContext);
            this.getAuthenticationService().authenticate(authenticationContext);
        } catch (IOException e) {
            throw new BotCommandException(LabelHelper.getString(CommandMessages.ERROR_AUTH_FAILED));
        } catch (Exception e) {
            LOGGER.error("Exception during user authorization: ", e);
            throw new BotCommandException(e.getMessage(), e);
        }
    }

    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }

    @Override
    protected Object executeCommandLogic(List<Object> args) throws Exception {
        return null;
    }

    @Override
    protected AuthenticationService initService(AuthenticationContext context) throws IOException {
        return null;
    }
}
