/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.utilities;

import com.automationanywhere.botcommand.constants.CommandMessages;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;

public class LabelHelper {
    private static final Messages MESSAGES =
            MessagesFactory.getMessages(CommandMessages.RESOURCE_FILE);

    public static String getString(String code) {
        return MESSAGES.getString(code);
    }

    public static String getString(String code, Object... objects) {
        return MESSAGES.getString(code, objects);
    }
}
