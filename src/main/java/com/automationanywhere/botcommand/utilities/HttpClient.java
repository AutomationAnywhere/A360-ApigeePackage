/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.utilities;

import static com.automationanywhere.botcommand.constants.Endpoints.Method.GET;

import com.automationanywhere.bot.model.ProxyConfig;
import com.automationanywhere.botcommand.constants.CommandMessages;
import com.automationanywhere.botcommand.constants.Endpoints;
import com.automationanywhere.botcommand.constants.InternalCode;
import com.automationanywhere.botcommand.exceptions.HttpClientException;
import com.automationanywhere.botcommand.exceptions.InternalException;
import com.automationanywhere.restsdk.client.RestClient;
import com.automationanywhere.restsdk.config.ClientBuilderApi;
import com.automationanywhere.restsdk.config.Execute;
import com.automationanywhere.restsdk.config.RequestBodyUriSpec;
import com.automationanywhere.restsdk.config.RequestUriSpec;
import com.automationanywhere.restsdk.data.RestResponse;
import java.io.IOException;
import java.util.Map;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpClient {
    private RestClient client;
    private ProxyConfig proxyConfig;
    private final int MAX_RETRY = 3;
    private final int MAX_TIMEOUT = 600000;

    protected static Logger LOGGER = LogManager.getLogger();

    public HttpClient() {}

    public HttpClient(ProxyConfig proxyConfig) {
        this.client = new RestClient();
        this.proxyConfig = proxyConfig;
    }

    public HttpClient(RestClient client) {
        this.client = client;
    }

    public void send(String env, String endpointUrl, Endpoints.Method method) throws IOException {
        send(env, endpointUrl, method, null, null, Void.class);
    }

    public <T> T send(String env, String endpointUrl, Endpoints.Method method, Class<T> objectClass)
            throws IOException {
        return send(env, endpointUrl, method, null, null, objectClass);
    }

    public <T> T send(
            String env,
            String endpointUrl,
            Endpoints.Method method,
            String body,
            Class<T> objectClass)
            throws IOException {
        return send(env, endpointUrl, method, body, null, objectClass);
    }

    public <T> T send(
            String env,
            String endpointUrl,
            Endpoints.Method method,
            String body,
            Map<String, String> headers,
            Class<T> objectClass)
            throws IOException {
        RequestUriSpec uriRequest = null;
        switch (method) {
            case POST:
                uriRequest = this.client.post();
                break;
            case PUT:
                uriRequest = this.client.put();
                break;
            case GET:
                uriRequest = this.client.get();
                break;
            case DELETE:
                uriRequest = this.client.delete();
                break;
            case PATCH:
                uriRequest = this.client.patch();
                break;
            default:
                throw new InternalException(InternalCode.Errors.InvalidHttpMethod);
        }

        String restUrl = UrlUtility.encodingQueryStrings(env + endpointUrl);

        LOGGER.info("Request Url - {} {}", method, restUrl);

        uriRequest.uri(restUrl);
        uriRequest.header("Content-Type", ContentType.APPLICATION_JSON.toString());

        if (headers != null && !headers.isEmpty()) {
            for (String key : headers.keySet()) {
                uriRequest.header(key, headers.get(key));
            }
        }

        if (!StringUtility.isNullOrEmpty(body)) {
            if (method != GET) {
                ((RequestBodyUriSpec) uriRequest).body(body);
            }
        }

        if (this.proxyConfig != null) {
            ((ClientBuilderApi<?>) uriRequest).proxySelector(this.proxyConfig.getProxySelector());
        }

        ((ClientBuilderApi<?>) uriRequest).retry(MAX_RETRY);
        RestResponse response = ((Execute<RestResponse>) uriRequest.timeOut(MAX_TIMEOUT)).execute();
        int status = response.getStatusCode();

        LOGGER.info("Response Status: {} ", status);

        if (status == 200 || status == 201 || status == 202 || status == 204) {
            String responseBody = response.body();
            byte[] responseBytes = response.binaryBody();

            if (objectClass == void.class
                    || objectClass == Void.class
                    || (StringUtility.isNullOrEmpty(responseBody) && responseBytes == null)) {
                return null;
            }

            if (objectClass == String.class) {
                return (T) responseBody;
            } else if (objectClass == byte[].class) {
                return (T) responseBytes;
            } else {
                return JsonSerializer.deserialize(responseBody, objectClass);
            }
        } else {
            throw new HttpClientException(
                    LabelHelper.getString(CommandMessages.ERROR_APIGEE_API_FAILED), response);
        }
    }
}
