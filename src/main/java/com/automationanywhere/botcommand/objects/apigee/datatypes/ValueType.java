/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.objects.apigee.datatypes;

import com.automationanywhere.botcommand.utilities.JsonSerializer;

public class ValueType {
    public String stringValue;

    public Integer intValue;

    public Double doubleValue;

    public Boolean booleanValue;

    public String jsonValue;

    public StringArray stringArray;

    public IntArray intArray;

    public DoubleArray doubleArray;

    public BooleanArray booleanArray;

    public String toString() {
        if (stringValue != null) {
            return stringValue;
        }
        if (intValue != null) {
            return intValue.toString();
        }
        if (doubleValue != null) {
            return doubleValue.toString();
        }
        if (booleanValue != null) {
            return booleanValue.toString();
        }
        if (jsonValue != null) {
            return jsonValue;
        }
        if (stringArray != null) {
            return JsonSerializer.serialize(stringArray.stringValues);
        }
        if (intArray != null) {
            return JsonSerializer.serialize(intArray.intValues);
        }
        if (doubleArray != null) {
            return JsonSerializer.serialize(doubleArray.doubleValues);
        }
        if (booleanArray != null) {
            return JsonSerializer.serialize(booleanArray.booleanValues);
        }
        return null;
    }
}
