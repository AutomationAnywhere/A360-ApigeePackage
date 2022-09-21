/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.utilities;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class StringUtility {
    public static Boolean isNullOrEmpty(String data) {
        if (data == null) {
            return true;
        }

        return StringUtils.isBlank(data.trim());
    }

    public static Boolean isListNullOrEmpty(List<String> data) {
        if (data == null || data.size() == 0) {
            return true;
        }

        for (String item : data) {
            if (isNullOrEmpty(item)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isNotNullOrEmpty(Map<String, Object> fields, String key) {
        return (isNotNull(fields, key) && !StringUtility.isNullOrEmpty(fields.get(key).toString()));
    }

    public static boolean isNotNull(Map<String, Object> fields, String key) {
        return (fields.containsKey(key) && fields.get(key) != null);
    }

    public static boolean isAnyNotNull(Map<String, Object> fields, List<String> keys) {
        int count = 0;
        for (String key : keys) {
            if (fields.containsKey(key) && fields.get(key) != null) {
                count++;
            }
        }

        return keys.size() == count;
    }
}
