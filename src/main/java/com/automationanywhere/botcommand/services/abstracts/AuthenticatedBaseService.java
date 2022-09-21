/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.services.abstracts;

import com.automationanywhere.bot.service.GlobalSessionContext;
import com.automationanywhere.botcommand.constants.Endpoints;
import com.automationanywhere.botcommand.objects.AuthenticationContext;
import com.automationanywhere.botcommand.services.AuthenticationService;
import com.automationanywhere.botcommand.utilities.HttpClient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class AuthenticatedBaseService extends BaseService {
    private AuthenticationContext authenticationContext;
    protected AuthenticationService authenticationService;

    protected AuthenticatedBaseService(
            AuthenticationContext authenticationContext,
            GlobalSessionContext globalSessionContext) {
        super("");
        authenticationService = new AuthenticationService(globalSessionContext);
        this.authenticationContext = authenticationContext;
    }

    protected AuthenticatedBaseService(
            AuthenticationService authenticationService, HttpClient client) {
        super("");
        this.authenticationService = authenticationService;
        this.client = client;
    }

    public AuthenticationContext getAuthenticationContext() {
        return authenticationContext;
    }

    @Override
    protected <T> T sendRequest(
            String endpointUrl,
            Endpoints.Method method,
            String body,
            Map<String, String> headers,
            Class<T> objectClass)
            throws IOException {
        var credential = authenticationService.authenticate(authenticationContext);
        if (credential != null) {
            headers.put(
                    "Authorization", String.format("%s %s", "Bearer", credential.getAccessToken()));
        }
        // Because the response is chunked in some cases, we need to add this header to prevent the
        // exception "java.io.EOFException: Unexpected end of ZLIB input stream" when reading the
        // response.
        headers.put("Accept-Encoding", "deflate");
        return super.sendRequest(endpointUrl, method, body, headers, objectClass);
    }

    @Override
    protected <T> T sendRequest(
            String endpointUrl, Endpoints.Method method, String body, Class<T> objectClass)
            throws IOException {
        return sendRequest(endpointUrl, method, body, new HashMap<>(), objectClass);
    }

    @Override
    protected <T> T sendRequest(String endpointUrl, Endpoints.Method method, Class<T> objectClass)
            throws IOException {
        return sendRequest(endpointUrl, method, null, objectClass);
    }
}
