/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.utilities;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UrlUtility {
    public static Map<String, String> parseQueryParams(String queryString) {
        Map<String, String> queryMap = new LinkedHashMap<>();
        if (StringUtility.isNullOrEmpty(queryString)) {
            return queryMap;
        }

        List<String> queryComponents = new ArrayList<>();
        Boolean foundEqualSign = false;
        int endIndex = queryString.length();
        for (int i = queryString.length() - 1; i >= 0; i--) {
            if (queryString.charAt(i) == '=') {
                foundEqualSign = true;
            } else if (queryString.charAt(i) == '&') {
                if (foundEqualSign) {
                    queryComponents.add(
                            queryString.substring(
                                    i + 1,
                                    endIndex == queryString.length() ? endIndex : endIndex + 1));

                    // reset the flags for next parameter
                    endIndex = i - 1;
                    foundEqualSign = false;
                }
            } else if (i == 0) {
                queryComponents.add(
                        queryString.substring(
                                i, endIndex == queryString.length() ? endIndex : endIndex + 1));
            }
        }

        for (int i = queryComponents.size() - 1; i >= 0; i--) {
            String param = queryComponents.get(i);
            String[] keyValue = param.split("=");
            if (keyValue.length > 1) {
                String value = keyValue[1];
                for (int j = 2; j < keyValue.length; j++) {
                    value += "=" + keyValue[j];
                }
                queryMap.put(keyValue[0], value);
            }
        }

        return queryMap;
    }

    public static String mapQueryString(Map<String, String> queryParams) {
        List<String> querySet = new ArrayList<>();
        for (Map.Entry entry : queryParams.entrySet()) {
            querySet.add(String.format("%s=%s", entry.getKey(), entry.getValue()));
        }

        return String.join("&", querySet);
    }

    public static String encodingQueryStrings(String url) {
        String[] restUrlParts = url.split("\\?");
        if (restUrlParts.length > 1) {
            return restUrlParts[0] + "?" + encodingQueryParameters(restUrlParts[1]);
        }

        return url;
    }

    public static String encodingQueryParameters(String parameters) {
        Map<String, String> queries = parseQueryParams(parameters);
        if (!queries.isEmpty()) {
            for (Map.Entry entry : queries.entrySet()) {
                entry.setValue(
                        URLEncoder.encode((String) entry.getValue(), StandardCharsets.UTF_8));
            }
            return mapQueryString(queries);
        }

        return parameters;
    }

    public static String removeEndSlash(String url) {
        if (StringUtility.isNullOrEmpty(url)) {
            return url;
        }

        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }

        return url;
    }
}
