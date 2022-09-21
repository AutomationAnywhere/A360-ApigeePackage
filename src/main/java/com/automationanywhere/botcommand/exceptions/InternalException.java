/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.exceptions;

import com.automationanywhere.botcommand.constants.InternalCode;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.botcommand.utilities.LabelHelper;

public class InternalException extends BotCommandException {
    private InternalCode.Errors error;

    public InternalException(InternalCode.Errors error) {
        super(LabelHelper.getString("info.internalErrorInfo", error.getCode()));
        this.error = error;
    }

    public InternalCode.Errors getError() {
        return error;
    }
}
