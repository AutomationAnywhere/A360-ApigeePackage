/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.exceptions;

import com.automationanywhere.botcommand.constants.CommandMessages;
import com.automationanywhere.botcommand.utilities.JsonSerializer;
import com.automationanywhere.botcommand.utilities.LabelHelper;
import com.automationanywhere.botcommand.utilities.StringUtility;
import com.automationanywhere.restsdk.data.RestResponse;
import java.io.IOException;
import java.util.Map;

public class HttpClientException extends IOException {
    private String responseBody;
    private int responseStatus;

    public HttpClientException(String message, RestResponse response) {
        super(message);
        if (response != null) {
            this.responseStatus = response.getStatusCode();
            this.responseBody = response.body();
        }
    }

    public HttpClientException(String message, Map<String, Object> responseBody) {
        super(message);
        if (responseBody != null) {
            this.responseBody = JsonSerializer.serialize(responseBody);
        }
    }

    @Override
    public String getMessage() {
        String statusError = String.format("Status: %s", this.responseStatus);
        String detailError = LabelHelper.getString(CommandMessages.ERROR_UNKNOWN);

        if (!StringUtility.isNullOrEmpty(this.responseBody)) {
            detailError = String.format("Details: %s", this.responseBody);
        }

        return String.format("%s\n\n%s\n%s", super.getMessage(), statusError, detailError);
    }

    public String getResponseBody() {
        return responseBody;
    }

    public int getResponseStatus() {
        return responseStatus;
    }
}
