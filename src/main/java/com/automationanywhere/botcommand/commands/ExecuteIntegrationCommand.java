/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.commands;

import static com.automationanywhere.commandsdk.model.AttributeType.*;
import static com.automationanywhere.commandsdk.model.DataType.DICTIONARY;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.botcommand.objects.AuthenticationContext;
import com.automationanywhere.botcommand.services.IntegrationService;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListAddButtonLabel;
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListEmptyLabel;
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListLabel;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.SelectModes;
import com.automationanywhere.commandsdk.model.AttributeType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@BotCommand
@CommandPkg(
        label = "[[ExecuteIntegration.label]]",
        description = "[[ExecuteIntegration.description]]",
        name = "ExecuteIntegration",
        node_label = "[[ExecuteIntegration.node_label]]",
        icon = "apigee.svg",
        return_label = "[[ExecuteIntegration.return_label]]",
        return_description = "[[ExecuteIntegration.return_description]]",
        return_type = DICTIONARY)
public class ExecuteIntegrationCommand extends BaseCommand<IntegrationService> {
    @Sessions private Map<String, Object> sessionMap;

    @GlobalSessionContext
    private com.automationanywhere.bot.service.GlobalSessionContext globalSessionContext;

    public void setGlobalSessionContext(
            com.automationanywhere.bot.service.GlobalSessionContext globalSessionContext) {
        this.globalSessionContext = globalSessionContext;
    }

    @Idx(index = "1", type = DESKTOPOPERATIONBUTTON)
    @Pkg(
            label = "[[ExecuteIntegration.discover.button.label]]",
            desktop_operation_name = "integrationDiscovery")
    String configurationButton;

    @Execute
    public DictionaryValue execute(
            @Idx(index = "2", type = TEXT)
                    @Pkg(
                            label = "[[ExecuteIntegration.project.id.label]]",
                            description = "[[ExecuteIntegration.project.id.description]]")
                    @NotEmpty
                    String projectId,
            @Idx(
                            index = "3",
                            type = SELECT,
                            options = {
                                @Idx.Option(index = "3.1", pkg = @Pkg(label = "us", value = "us")),
                                @Idx.Option(
                                        index = "3.2",
                                        pkg =
                                                @Pkg(
                                                        label = "northamerica-northeast1",
                                                        value = "northamerica-northeast1")),
                                @Idx.Option(
                                        index = "3.3",
                                        pkg = @Pkg(label = "europe-west2", value = "europe-west2")),
                                @Idx.Option(
                                        index = "3.4",
                                        pkg =
                                                @Pkg(
                                                        label = "asia-southeast1",
                                                        value = "asia-southeast1")),
                                @Idx.Option(
                                        index = "3.5",
                                        pkg = @Pkg(label = "asia-south1", value = "asia-south1")),
                                @Idx.Option(
                                        index = "3.6",
                                        pkg =
                                                @Pkg(
                                                        label = "australia-southeast1",
                                                        value = "australia-southeast1")),
                                @Idx.Option(
                                        index = "3.7",
                                        pkg = @Pkg(label = "us-central1", value = "us-central1")),
                                @Idx.Option(
                                        index = "3.8",
                                        pkg = @Pkg(label = "us-east1", value = "us-east1")),
                                @Idx.Option(
                                        index = "3.9",
                                        pkg = @Pkg(label = "us-west1", value = "us-west1")),
                                @Idx.Option(
                                        index = "3.10",
                                        pkg = @Pkg(label = "europe-west1", value = "europe-west1")),
                                @Idx.Option(
                                        index = "3.11",
                                        pkg = @Pkg(label = "europe-west3", value = "europe-west3")),
                                @Idx.Option(
                                        index = "3.12",
                                        pkg = @Pkg(label = "asia-east1", value = "asia-east1")),
                                @Idx.Option(
                                        index = "3.13",
                                        pkg =
                                                @Pkg(
                                                        label = "southamerica-east1",
                                                        value = "southamerica-east1")),
                                @Idx.Option(
                                        index = "3.14",
                                        pkg = @Pkg(label = "us-east4", value = "us-east4")),
                                @Idx.Option(
                                        index = "3.15",
                                        pkg = @Pkg(label = "us-west2", value = "us-west2")),
                                @Idx.Option(
                                        index = "3.16",
                                        pkg =
                                                @Pkg(
                                                        label = "asia-northeast1",
                                                        value = "asia-northeast1")),
                                @Idx.Option(
                                        index = "3.17",
                                        pkg = @Pkg(label = "europe-west4", value = "europe-west4")),
                                @Idx.Option(
                                        index = "3.18",
                                        pkg = @Pkg(label = "europe-west6", value = "europe-west6")),
                            })
                    @Pkg(
                            label = "[[ExecuteIntegration.location.label]]",
                            description = "[[ExecuteIntegration.location.description]]")
                    @NotEmpty
                    String location,
            @Idx(index = "4", type = TEXT)
                    @Pkg(
                            label = "[[ExecuteIntegration.integration.name.label]]",
                            description = "[[ExecuteIntegration.integration.name.description]]",
                            default_value_type = STRING)
                    @NotEmpty
                    String integrationName,
            @Idx(index = "5", type = TEXT)
                    @Pkg(
                            label = "[[ExecuteIntegration.trigger.id.label]]",
                            description = "[[ExecuteIntegration.trigger.id.description]]",
                            default_value_type = STRING)
                    @NotEmpty
                    String triggerId,
            @Idx(index = "6.4", type = TEXT, name = "INPUT_PARAMETER_NAME")
                    @Pkg(
                            label = "[[ExecuteIntegration.entryList.dialogue.fieldName.label]]",
                            default_value_type = STRING)
                    @NotEmpty
                    String inputParameterName,
            @Idx(index = "6.5", type = TEXT, name = "INPUT_PARAMETER_TYPE")
                    @Pkg(
                            label = "[[ExecuteIntegration.entryList.dialogue.fieldType.label]]",
                            default_value_type = STRING,
                            readOnly = true)
                    String inputParameterType,
            @Idx(index = "6.6", type = TEXT, name = "INPUT_PARAMETER_VALUE")
                    @Pkg(
                            label = "[[ExecuteIntegration.entryList.dialogue.fieldValue.label]]",
                            default_value_type = STRING)
                    String inputParameterValue,
            @Idx(
                            index = "6",
                            type = AttributeType.ENTRYLIST,
                            options = {
                                @Idx.Option(
                                        index = "6.1",
                                        pkg =
                                                @Pkg(
                                                        title = "INPUT_PARAMETER_NAME",
                                                        label =
                                                                "[[ExecuteIntegration.entryList.fieldName.label]]")),
                                @Idx.Option(
                                        index = "6.2",
                                        pkg =
                                                @Pkg(
                                                        title = "INPUT_PARAMETER_TYPE",
                                                        label =
                                                                "[[ExecuteIntegration.entryList.fieldType.label]]",
                                                        readOnly = true)),
                                @Idx.Option(
                                        index = "6.3",
                                        pkg =
                                                @Pkg(
                                                        title = "INPUT_PARAMETER_VALUE",
                                                        label =
                                                                "[[ExecuteIntegration.entryList.fieldValue.label]]"))
                            })
                    @Pkg(
                            label = "[[ExecuteIntegration.entryList.label]]",
                            description = "[[ExecuteIntegration.entryList.description]]")
                    @SelectModes
                    @EntryListLabel(value = "[[ExecuteIntegration.entryList.dialogue.label]]")
                    @EntryListAddButtonLabel(
                            value = "[[ExecuteIntegration.entryList.addButton.label]]")
                    @EntryListEmptyLabel(value = "[[ExecuteIntegration.entryList.empty.label]]")
                    @Inject
                    List<Value> inputParameters,
            @Idx(index = "7", type = TEXT)
                    @Pkg(
                            label = "[[common.session.label]]",
                            default_value_type = STRING,
                            default_value = "Default")
                    @NotEmpty
                    String sessionName) {
        try {
            this.setSession(this.sessionMap, sessionName, this.globalSessionContext);
            Map<String, StringValue> fields = new HashMap<>();
            if (inputParameters != null) {
                inputParameters.forEach(
                        (value) -> {
                            var dictValue = (DictionaryValue) value;
                            String fieldName = null;
                            StringValue fieldValue = null;
                            for (Map.Entry entry : dictValue.get().entrySet()) {
                                var entryValue = (StringValue) entry.getValue();
                                var entryKey = entry.getKey().toString();
                                if (entryKey.equals("INPUT_PARAMETER_NAME")) {
                                    fieldName = entryValue.get();
                                } else if (entryKey.equals("INPUT_PARAMETER_VALUE")) {
                                    fieldValue = entryValue;
                                }
                            }
                            fields.put(fieldName, fieldValue);
                        });
            }

            return executeCommand(
                    Arrays.asList(projectId, location, integrationName, triggerId, fields),
                    DictionaryValue.class);

        } catch (Exception e) {
            LOGGER.error("Exception during user authorization: {}", e);
            throw new BotCommandException(e.getMessage(), e);
        }
    }

    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }

    @Override
    protected Object executeCommandLogic(List<Object> args) throws Exception {
        String projectId = String.valueOf(args.get(0));
        String location = String.valueOf(args.get(1));
        String integrationName = String.valueOf(args.get(2));
        String triggerId = String.valueOf(args.get(3));
        Map<String, Value> fields = (Map<String, Value>) args.get(4);
        return this.getService()
                .executeIntegration(projectId, location, integrationName, triggerId, fields);
    }

    @Override
    protected IntegrationService initService(AuthenticationContext context) {
        IntegrationService service = new IntegrationService(context, this.globalSessionContext);
        return service;
    }
}
