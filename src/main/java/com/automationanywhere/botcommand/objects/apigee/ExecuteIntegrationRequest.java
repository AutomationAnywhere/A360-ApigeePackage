/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.objects.apigee;

import com.automationanywhere.botcommand.objects.apigee.datatypes.ValueType;
import java.util.Map;

public class ExecuteIntegrationRequest {
    private String name;

    private String triggerId;

    private Map<String, ValueType> inputParameters;

    public ExecuteIntegrationRequest(
            String name, String triggerId, Map<String, ValueType> inputParameters) {
        this.name = name;
        this.triggerId = triggerId;
        this.inputParameters = inputParameters;
    }
}
