/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.services;

import com.automationanywhere.bot.service.GlobalSessionContext;
import com.automationanywhere.botcommand.constants.Endpoints;
import com.automationanywhere.botcommand.objects.AuthenticationContext;
import com.automationanywhere.botcommand.objects.apigee.OrganizationListResponse;
import com.automationanywhere.botcommand.services.abstracts.AuthenticatedBaseService;
import com.automationanywhere.botcommand.utilities.HttpClient;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class OrganizationService extends AuthenticatedBaseService {
    public OrganizationService(
            AuthenticationContext authenticationContext,
            GlobalSessionContext globalSessionContext) {
        super(authenticationContext, globalSessionContext);
    }

    protected OrganizationService(AuthenticationService authenticationService, HttpClient client) {
        super(authenticationService, client);
    }

    public List<String> listProjectIds() throws IOException {
        return sendRequest(
                        Endpoints.ORGANIZATIONS_ENDPOINT,
                        Endpoints.Method.GET,
                        OrganizationListResponse.class)
                .organizations.stream()
                .map(o -> o.projectId)
                .collect(Collectors.toList());
    }
}
