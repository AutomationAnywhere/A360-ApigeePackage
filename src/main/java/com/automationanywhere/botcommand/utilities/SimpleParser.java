/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.utilities;

public class SimpleParser {
    public static <T> ParserResult<T> tryParse(Object obj, Class<T> castType) {
        try {
            if (obj != null) {
                return new ParserResult<T>(castType.cast(obj));
            } else {
                return new ParserResult<T>(false, (T) null);
            }
        } catch (Exception ex) {
            return new ParserResult<T>(ex);
        }
    }

    public static class ParserResult<T> {
        private boolean isParsed;
        private T parsedValue;
        private Exception exception;

        public ParserResult(boolean isParse, T parsedValue) {
            this.isParsed = isParse;
            this.parsedValue = parsedValue;
        }

        public ParserResult(T parsedValue) {
            this.isParsed = true;
            this.parsedValue = parsedValue;
        }

        public ParserResult(Exception exception) {
            this.isParsed = false;
            this.exception = exception;
        }

        public boolean isParsed() {
            return isParsed;
        }

        public T getParsedValue() {
            return parsedValue;
        }

        public Exception getException() {
            return exception;
        }
    }
}
