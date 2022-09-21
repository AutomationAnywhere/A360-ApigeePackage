/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.stages;

import static com.automationanywhere.commandsdk.model.AttributeType.DESKTOPOPERATIONSELECT;
import static com.automationanywhere.commandsdk.model.AttributeType.SELECT;

import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.DataType;

@BotCommand(commandType = BotCommand.CommandType.DESKTOP_OPERATION_STAGE)
@CommandPkg(
        name = "configurationStage",
        desktop_operation_name = "integrationDiscovery",
        label = "[[Discovery.config.stage.label]]",
        submit_label = "[[Discovery.config.stage.submit.label]]",
        remember = true)
public class ConfigurationStage {
    @Idx(index = "1", type = DESKTOPOPERATIONSELECT)
    @Pkg(
            label = "[[Discovery.config.stage.projectId.label]]",
            desktop_operation_name = "GetProjectIds")
    @NotEmpty
    String projectId;

    @Idx(
            index = "2",
            type = SELECT,
            options = {
                @Idx.Option(index = "2.1", pkg = @Pkg(label = "all locations", value = "all")),
                @Idx.Option(index = "2.2", pkg = @Pkg(label = "us", value = "us")),
                @Idx.Option(
                        index = "2.3",
                        pkg =
                                @Pkg(
                                        label = "northamerica-northeast1",
                                        value = "northamerica-northeast1")),
                @Idx.Option(
                        index = "2.4",
                        pkg = @Pkg(label = "europe-west2", value = "europe-west2")),
                @Idx.Option(
                        index = "2.5",
                        pkg = @Pkg(label = "asia-southeast1", value = "asia-southeast1")),
                @Idx.Option(
                        index = "2.6",
                        pkg = @Pkg(label = "asia-south1", value = "asia-south1")),
                @Idx.Option(
                        index = "2.7",
                        pkg = @Pkg(label = "australia-southeast1", value = "australia-southeast1")),
                @Idx.Option(
                        index = "2.8",
                        pkg = @Pkg(label = "us-central1", value = "us-central1")),
                @Idx.Option(index = "2.9", pkg = @Pkg(label = "us-east1", value = "us-east1")),
                @Idx.Option(index = "2.10", pkg = @Pkg(label = "us-west1", value = "us-west1")),
                @Idx.Option(
                        index = "2.11",
                        pkg = @Pkg(label = "europe-west1", value = "europe-west1")),
                @Idx.Option(
                        index = "2.12",
                        pkg = @Pkg(label = "europe-west3", value = "europe-west3")),
                @Idx.Option(index = "2.13", pkg = @Pkg(label = "asia-east1", value = "asia-east1")),
                @Idx.Option(
                        index = "2.14",
                        pkg = @Pkg(label = "southamerica-east1", value = "southamerica-east1")),
                @Idx.Option(index = "2.15", pkg = @Pkg(label = "us-east4", value = "us-east4")),
                @Idx.Option(index = "2.16", pkg = @Pkg(label = "us-west2", value = "us-west2")),
                @Idx.Option(
                        index = "2.17",
                        pkg = @Pkg(label = "asia-northeast1", value = "asia-northeast1")),
                @Idx.Option(
                        index = "2.18",
                        pkg = @Pkg(label = "europe-west4", value = "europe-west4")),
                @Idx.Option(
                        index = "2.19",
                        pkg = @Pkg(label = "europe-west6", value = "europe-west6")),
            })
    @Pkg(
            label = "[[Discovery.config.stage.location.label]]",
            default_value = "all",
            default_value_type = DataType.STRING)
    @NotEmpty
    String location;
}
