/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.constants;

public class InternalCode {
    public enum Errors {
        SessionIsNull(100),
        AuthContextOrPropsIsNull(101),
        InvalidHttpMethod(105);

        private int code;

        Errors(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}
