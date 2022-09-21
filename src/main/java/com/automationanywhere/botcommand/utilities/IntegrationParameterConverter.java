/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.utilities;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.botcommand.objects.apigee.IntegrationParameter;
import com.automationanywhere.botcommand.objects.apigee.datatypes.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntegrationParameterConverter {
    public static Map<String, ValueType> convertInputParameters(
            Map<String, Value> inputParameters, List<IntegrationParameter> integrationParameters) {
        Map<String, ValueType> params = new HashMap<>();
        integrationParameters.forEach(
                (integrationParameter -> {
                    if (inputParameters.containsKey(integrationParameter.displayName)) {
                        Value val = inputParameters.get(integrationParameter.displayName);
                        ValueType valueType = new ValueType();
                        try {
                            switch (integrationParameter.dataType) {
                                case "INT_VALUE":
                                    valueType.intValue = Integer.parseInt((String) val.get());
                                    break;
                                case "STRING_VALUE":
                                    valueType.stringValue = (String) val.get();
                                    break;
                                case "DOUBLE_VALUE":
                                    valueType.doubleValue = Double.valueOf((String) val.get());
                                    break;
                                case "BOOLEAN_VALUE":
                                    valueType.booleanValue =
                                            Boolean.parseBoolean((String) val.get());
                                    break;
                                case "INT_ARRAY":
                                    IntArray intArr = new IntArray();
                                    intArr.intValues =
                                            JsonSerializer.deserialize(
                                                    (String) val.get(), int[].class);
                                    valueType.intArray = intArr;
                                    break;
                                case "STRING_ARRAY":
                                    StringArray strArr = new StringArray();
                                    strArr.stringValues =
                                            JsonSerializer.deserialize(
                                                    (String) val.get(), String[].class);
                                    valueType.stringArray = strArr;
                                    break;
                                case "DOUBLE_ARRAY":
                                    DoubleArray doubleArr = new DoubleArray();
                                    doubleArr.doubleValues =
                                            JsonSerializer.deserialize(
                                                    (String) val.get(), double[].class);
                                    valueType.doubleArray = doubleArr;
                                    break;
                                case "BOOLEAN_ARRAY":
                                    BooleanArray boolArr = new BooleanArray();
                                    boolArr.booleanValues =
                                            JsonSerializer.deserialize(
                                                    (String) val.get(), boolean[].class);
                                    valueType.booleanArray = boolArr;
                                    break;
                                case "JSON_VALUE":
                                    valueType.jsonValue = (String) val.get();
                                    break;
                                default:
                                    throw new BotCommandException(
                                            "Internal Error: case undefined for integrationParameter.dataType '"
                                                    + integrationParameter.dataType
                                                    + "'.");
                            }
                        } catch (Exception e) {
                            throw new BotCommandException(
                                    "Error parsing parameters, possibly due to incorrect JSON list syntax used in input. Full error: "
                                            + e.getMessage());
                        }

                        params.put(integrationParameter.displayName, valueType);
                    }
                }));
        return params;
    }
}
