/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.services;

import com.automationanywhere.bot.service.GlobalSessionContext;
import com.automationanywhere.botcommand.constants.CommandMessages;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.botcommand.objects.AuthenticationContext;
import com.automationanywhere.botcommand.services.abstracts.BaseService;
import com.automationanywhere.botcommand.utilities.LabelHelper;
import com.automationanywhere.botcommand.utilities.StringUtility;
import com.google.api.client.auth.oauth2.Credential;
import java.io.IOException;
import java.security.InvalidParameterException;

public class AuthenticationService extends BaseService {
    private GlobalSessionContext globalSessionContext;
    private GSuiteAuthenticationService gSuiteAuthenticationService;

    public AuthenticationService(GlobalSessionContext globalSessionContext) {
        this.globalSessionContext = globalSessionContext;
        this.gSuiteAuthenticationService = new GSuiteAuthenticationService();
    }

    public Credential authenticate(AuthenticationContext authenticationContext) throws IOException {

        var clientId = authenticationContext.getClientId();
        if (clientId == null || StringUtility.isNullOrEmpty(clientId.getInsecureString())) {
            throw new InvalidParameterException(CommandMessages.ERROR_INVALID_CLIENT_ID);
        }

        var clientSecret = authenticationContext.getClientSecret();
        if (clientSecret == null || StringUtility.isNullOrEmpty(clientSecret.getInsecureString())) {
            throw new InvalidParameterException(CommandMessages.ERROR_INVALID_CLIENT_SECRET);
        }

        var userEmailAddress = authenticationContext.getUserEmailAddress();
        if (userEmailAddress == null
                || StringUtility.isNullOrEmpty(userEmailAddress.getInsecureString())) {
            throw new InvalidParameterException(CommandMessages.ERROR_INVALID_USERNAME);
        }

        var redirectUri = authenticationContext.getRedirectUri();
        if (redirectUri == null || StringUtility.isNullOrEmpty(redirectUri.getInsecureString())) {
            throw new InvalidParameterException(CommandMessages.ERROR_INVALID_REDIRECT_URI);
        }

        LOGGER.info("Launching consent screen...");
        try {
            return gSuiteAuthenticationService.authenticate(
                    authenticationContext, globalSessionContext);
        } catch (IOException e) {
            throw new BotCommandException(LabelHelper.getString(CommandMessages.ERROR_AUTH_FAILED));
        }
    }
}
