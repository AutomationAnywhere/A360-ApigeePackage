/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.commands;

import com.automationanywhere.bot.service.GlobalSessionContext;
import com.automationanywhere.botcommand.constants.CommandMessages;
import com.automationanywhere.botcommand.constants.InternalCode;
import com.automationanywhere.botcommand.constants.SessionConstants;
import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.botcommand.exceptions.ExceptionHandler;
import com.automationanywhere.botcommand.exceptions.InternalException;
import com.automationanywhere.botcommand.objects.AuthenticationContext;
import com.automationanywhere.botcommand.services.AuthenticationService;
import com.automationanywhere.botcommand.services.abstracts.BaseService;
import com.automationanywhere.botcommand.utilities.CommandDataTypeConverter;
import com.automationanywhere.botcommand.utilities.LabelHelper;
import com.automationanywhere.botcommand.utilities.StringUtility;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseCommand<T extends BaseService> {
    private Map<String, Object> sessionMap;
    private String sessionName;
    private GlobalSessionContext globalSessionContext;
    private AuthenticationContext context;
    private AuthenticationService authenticationService;
    private T service;

    protected static Logger LOGGER = LogManager.getLogger(BaseCommand.class);

    protected abstract Object executeCommandLogic(List<Object> args) throws Exception;

    protected void setSession(
            Map<String, Object> sessionMap,
            String sessionName,
            GlobalSessionContext globalSessionContext) {
        if (sessionMap == null) {
            throw new InternalException(InternalCode.Errors.SessionIsNull);
        }

        if (StringUtility.isNullOrEmpty(sessionName)) {
            throw new InvalidParameterException(
                    LabelHelper.getString(CommandMessages.ERROR_INVALID_SESSION_NAME));
        }

        this.globalSessionContext = globalSessionContext;
        this.sessionMap = sessionMap;
        this.sessionName = sessionName;
        this.setProxy();
    }

    protected void setAuthenticationContext(AuthenticationContext context) {
        if (context == null || context.getClientId() == null || context.getClientSecret() == null) {
            throw new InternalException(InternalCode.Errors.AuthContextOrPropsIsNull);
        }

        String sessionKey = getAuthenticationContextKey();
        this.sessionMap.put(sessionKey, context);
        this.setProxy();
    }

    protected AuthenticationService getAuthenticationService() {
        return this.authenticationService;
    }

    protected void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        this.setProxy();
    }

    protected AuthenticationContext findAuthenticationContext() throws IOException {
        return getAuthenticationContext(false);
    }

    protected AuthenticationContext getAuthenticationContext(boolean throwExceptionIfNull)
            throws IOException {
        String sessionKey = this.getAuthenticationContextKey();

        if (this.sessionMap != null && this.sessionMap.containsKey(sessionKey)) {
            this.context = (AuthenticationContext) this.sessionMap.get(sessionKey);
            if (this.context != null) {
                if (this.authenticationService == null) {
                    this.setAuthenticationService(new AuthenticationService(globalSessionContext));
                }
                this.setAuthenticationContext(context);
            }
        } else {
            this.context = null;
        }

        if (this.context == null && throwExceptionIfNull) {
            throw new BotCommandException(
                    LabelHelper.getString(CommandMessages.ERROR_AUTH_TOKEN_IS_NOT_FOUND));
        }

        return this.context;
    }

    protected String getAuthenticationContextKey() {
        return SessionConstants.SESSION_AUTH_PREFIX + this.sessionName;
    }

    protected void executeCommand(List<Object> args) {
        executeCommand(args, Void.class, 1);
    }

    protected <T> T executeCommand(List<Object> args, Class<T> objectClass) {
        return executeCommand(args, objectClass, -1);
    }

    protected <T> T executeCommand(
            List<Object> args, Class<T> objectClass, int objectConversionLevel) {
        LOGGER.info("Start executing command");

        if (objectClass == null) {
            objectClass = (Class<T>) Void.class;
        }

        try {
            LOGGER.info("Getting Apigee Authentication Context");

            // this will retrieve the authentication context from session
            this.context = this.getAuthenticationContext(true);

            if (this.service == null) {
                this.service = initService(this.context);
            }

            LOGGER.info("Execute command logic from device class");
            // Core execution here.
            Object data = executeCommandLogic(args);

            if (objectClass == Void.class || objectClass == void.class) {
                LOGGER.info("No output is returning");
                return null;
            } else {
                LOGGER.info(
                        "Output is converting to the Command Data Type: "
                                + data.getClass().getName());
                if (objectConversionLevel > 0) {
                    return CommandDataTypeConverter.convert(
                            data, objectClass, objectConversionLevel);
                } else {
                    return CommandDataTypeConverter.convertAll(data, objectClass);
                }
            }
        } catch (Exception e) {
            LOGGER.error("An unexpected error has occurred in the command package.");
            ExceptionHandler.evaluate(e);
        } finally {
            LOGGER.info("End executing command");
        }

        return null;
    }

    protected <T> List<T> convertListValue(List<Value> list, Class<T> classObject) {
        ArrayList<T> converted = new ArrayList<>();
        if (list.size() == 0) {
            return converted;
        }

        for (Value value : list) {
            converted.add((T) value.get());
        }

        return converted;
    }

    protected void logInfo(String txt) {
        log(txt, Level.INFO);
    }

    protected void logDebug(String txt) {
        log(txt, Level.DEBUG);
    }

    protected void log(String txt, Level level) {
        if (Level.DEBUG.equals(level)) {
            LOGGER.debug(txt);
            return;
        }
        LOGGER.info(txt);
    }

    protected abstract T initService(AuthenticationContext context) throws IOException;

    public T getService() {
        return service;
    }

    public void setService(T service) {
        this.service = service;
    }

    private void setProxy() {
        if (this.globalSessionContext != null
                && this.globalSessionContext.getProxyConfig() != null) {
            if (this.authenticationService != null) {
                this.authenticationService.setProxy(this.globalSessionContext.getProxyConfig());
            }

            if (this.service != null) {
                this.service.setProxy(this.globalSessionContext.getProxyConfig());
            }
        }
    }
}
