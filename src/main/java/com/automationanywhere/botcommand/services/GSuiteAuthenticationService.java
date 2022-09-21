/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.services;

import com.automationanywhere.bot.service.GlobalSessionContext;
import com.automationanywhere.botcommand.gsuite.service.AbstractAuthUtils;
import com.automationanywhere.botcommand.gsuite.service.AuthenticationService;
import com.automationanywhere.botcommand.gsuite.service.impl.AuthenticationServiceImpl;
import com.automationanywhere.botcommand.objects.AuthenticationContext;
import com.google.api.client.auth.oauth2.Credential;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GSuiteAuthenticationService extends AbstractAuthUtils {

    private static final String CUSTOM_SCOPE = "customScope";
    private static final List<String> SCOPES =
            new ArrayList<>() {
                {
                    add("https://www.googleapis.com/auth/cloud-platform");
                }
            };

    public Credential authenticate(
            AuthenticationContext authenticationContext, GlobalSessionContext globalSessionContext)
            throws IOException {
        return launchOAuthConsentScreen(
                authenticationContext.getUserEmailAddress(),
                authenticationContext.getClientId(),
                authenticationContext.getClientSecret(),
                authenticationContext.getRedirectUri(),
                globalSessionContext);
    }

    @Override
    protected AuthenticationService getAuthenticationService() {
        var authenticationService = new AuthenticationServiceImpl();
        authenticationService.setFeature(CUSTOM_SCOPE);
        authenticationService.setCustomScopes(SCOPES);
        return authenticationService;
    }
}
