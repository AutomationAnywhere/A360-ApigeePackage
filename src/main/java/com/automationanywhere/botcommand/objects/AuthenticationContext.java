/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.objects;

import com.automationanywhere.core.security.SecureString;

public class AuthenticationContext {
    private SecureString userEmailAddress;
    private SecureString clientId;
    private SecureString clientSecret;
    private SecureString redirectUri;

    public AuthenticationContext(
            SecureString userEmailAddress,
            SecureString clientId,
            SecureString clientSecret,
            SecureString redirectUri) {
        this.userEmailAddress = userEmailAddress;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    public SecureString getUserEmailAddress() {
        return userEmailAddress;
    }

    public SecureString getClientId() {
        return clientId;
    }

    public SecureString getClientSecret() {
        return clientSecret;
    }

    public SecureString getRedirectUri() {
        return redirectUri;
    }
}
