/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.exceptions;

import com.automationanywhere.botcommand.constants.CommandMessages;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.botcommand.utilities.LabelHelper;
import com.automationanywhere.botcommand.utilities.SimpleParser;
import java.security.InvalidParameterException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExceptionHandler {
    private static Logger LOGGER = LogManager.getLogger(ExceptionHandler.class);

    public static <T extends Exception> void evaluate(T e) {
        try {
            if (e instanceof HttpClientException) {
                SimpleParser.ParserResult<HttpClientException> handledEx =
                        SimpleParser.tryParse(e, HttpClientException.class);
                if (handledEx.isParsed()) {
                    throw new BotCommandException(
                            handledEx.getParsedValue().getMessage(), handledEx.getParsedValue());
                }
            } else if (e instanceof InternalException) {
                SimpleParser.ParserResult<InternalException> handledEx =
                        SimpleParser.tryParse(e, InternalException.class);
                if (handledEx.isParsed()) {
                    throw new BotCommandException(
                            String.format(
                                    LabelHelper.getString(CommandMessages.INFO_INTERNAL_ERROR_INFO),
                                    handledEx.getParsedValue().getErrorCode()),
                            handledEx.getParsedValue());
                }
            } else if (e instanceof InvalidParameterException) {
                SimpleParser.ParserResult<InvalidParameterException> handledEx =
                        SimpleParser.tryParse(e, InvalidParameterException.class);
                if (handledEx.isParsed()) {
                    String message =
                            String.format(
                                    "%s  %s",
                                    handledEx.getParsedValue().getMessage(),
                                    LabelHelper.getString(CommandMessages.INFO_VERIFY_YOUR_INPUT));
                    throw new BotCommandException(message);
                }
            } else if (e instanceof BotCommandException) {
                throw (BotCommandException) e;
            }
        } catch (Exception handlingEx) {
            if (handlingEx instanceof BotCommandException) {
                throw handlingEx;
            } else {
                // when error handling failed, output the original exception as it is.
                LOGGER.error(
                        "ExceptionHandler has failed to evaluate the exception.  Error: "
                                + handlingEx.getMessage(),
                        handlingEx);
            }
        }

        throw new BotCommandException(e.getMessage(), e);
    }
}
