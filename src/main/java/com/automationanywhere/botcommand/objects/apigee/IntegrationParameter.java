/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.objects.apigee;

import com.automationanywhere.botcommand.objects.apigee.datatypes.ValueType;
import com.automationanywhere.botcommand.utilities.LabelHelper;

public class IntegrationParameter {
    public String key;
    public String dataType;
    public ValueType defaultValue;
    public String displayName;
    public String inputOutputType;

    public String toString() {
        String key = "key: " + this.key;
        String dataType = "dataType: " + this.dataType;
        String displayName = "displayName: " + this.displayName;
        String inputOutputType = "inputOutputType: " + this.inputOutputType;
        return key
                + '\n'
                + displayName
                + '\n'
                + dataType
                + '\n'
                + displayName
                + '\n'
                + inputOutputType;
    }

    public String datatypeDisplayString() {
        return LabelHelper.getString("label.dataType." + this.dataType);
    }
}
