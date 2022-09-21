/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.constants;

public class Endpoints {
    public enum Method {
        GET,
        POST,
        DELETE,
        PATCH,
        PUT
    }

    public static final String INTEGRATIONS_API_BASE_URL =
            "https://%s-integrations.googleapis.com/v1/";

    public static final String LIST_INTEGRATIONS_ENDPOINT =
            "projects/%s/locations/%s/products/apigee/integrations";

    public static final String LIST_INTEGRATION_VERSIONS_ENDPOINT =
            "projects/%s/locations/%s/products/apigee/integrations/%s/versions";

    public static final String EXECUTE_INTEGRATIONS_ENDPOINT =
            "projects/%s/locations/%s/products/apigee/integrations/%s:execute";

    public static final String ORGANIZATIONS_ENDPOINT =
            "https://apigee.googleapis.com/v1/organizations/";
}
