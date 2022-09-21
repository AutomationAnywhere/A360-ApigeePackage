/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.utilities;

import com.google.gson.Gson;
import java.lang.reflect.Type;

public class JsonSerializer {
    public static String serialize(Object source) {
        if (source == null) {
            return null;
        }

        return new Gson().toJson(source);
    }

    public static <T> T deserialize(String source, Class<T> objectClass) {
        if (source == null) {
            return null;
        }

        return new Gson().fromJson(source, objectClass);
    }

    public static <T> T deserialize(String source, Type type) {
        if (source == null) {
            return null;
        }

        return new Gson().fromJson(source, type);
    }
}
