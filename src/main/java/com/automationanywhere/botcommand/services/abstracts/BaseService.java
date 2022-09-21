/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.services.abstracts;

import com.automationanywhere.bot.model.ProxyConfig;
import com.automationanywhere.botcommand.constants.Endpoints;
import com.automationanywhere.botcommand.utilities.HttpClient;
import java.io.IOException;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseService {
    private String env;
    protected HttpClient client;
    private int pageSize;
    protected final Logger LOGGER = LogManager.getLogger(BaseService.class);

    public String getEnvironment() {
        return env;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    protected BaseService() {
        this.client = new HttpClient();
    }

    protected BaseService(String env) {
        this.env = env;
        this.client = new HttpClient();
    }

    protected BaseService(String env, HttpClient client) {
        this.env = env;
        this.client = client;
    }

    public void setProxy(ProxyConfig proxyConfig) {
        this.client = new HttpClient(proxyConfig);
    }

    protected <T> T sendRequest(
            String endpointUrl,
            Endpoints.Method method,
            String body,
            Map<String, String> headers,
            Class<T> objectClass)
            throws IOException {
        return this.client.send(env, endpointUrl, method, body, headers, objectClass);
    }

    protected <T> T sendRequest(
            String endpointUrl, Endpoints.Method method, String body, Class<T> objectClass)
            throws IOException {
        return this.client.send(env, endpointUrl, method, body, objectClass);
    }

    protected <T> T sendRequest(String endpointUrl, Endpoints.Method method, Class<T> objectClass)
            throws IOException {
        return this.client.send(env, endpointUrl, method, objectClass);
    }
}
