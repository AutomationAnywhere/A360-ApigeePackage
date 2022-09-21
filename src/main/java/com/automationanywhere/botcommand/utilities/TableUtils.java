/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.utilities;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.objects.apigee.Integration;
import com.automationanywhere.botcommand.objects.apigee.IntegrationParameter;
import com.automationanywhere.botcore.api.dto.AttributeType;
import java.util.ArrayList;
import java.util.List;

public class TableUtils {
    public static TableValue convertIntegrationsToTable(List<Integration> integrations) {
        List<Schema> schemas = new ArrayList<>();
        schemas.add(new Schema("Name", AttributeType.STRING));
        schemas.add(new Schema("Description", AttributeType.STRING));
        schemas.add(new Schema("Location", AttributeType.STRING));
        schemas.add(new Schema("Active", AttributeType.BOOLEAN));
        schemas.add(new Schema("Update Time", AttributeType.DATETIME));

        List<Row> rows = new ArrayList<>();
        for (var integration : integrations) {
            List<Value> values = new ArrayList<>();
            var index = integration.name.lastIndexOf('/');
            var name = integration.name.substring(index + 1);
            values.add(new StringValue(name));
            values.add(new StringValue(integration.description));
            values.add(new StringValue(integration.location));
            values.add(new StringValue(String.valueOf(integration.active)));
            values.add(new StringValue(integration.updateTime));

            Row row = new Row();
            row.setValues(values);
            rows.add(row);
        }

        return new TableValue(new Table(schemas, rows));
    }

    public static TableValue convertParametersToTable(
            List<IntegrationParameter> integrationParameters) {
        List<Schema> schemas = new ArrayList<>();
        schemas.add(new Schema("Name", AttributeType.STRING));
        schemas.add(new Schema("Type", AttributeType.STRING));
        schemas.add(new Schema("Value", AttributeType.STRING));

        List<Row> rows = new ArrayList<>();
        for (var parameter : integrationParameters) {
            List<Value> values = new ArrayList<>();
            var defaultValue =
                    parameter.defaultValue != null ? parameter.defaultValue.toString() : "";
            values.add(new StringValue(parameter.displayName));
            values.add(new StringValue(parameter.datatypeDisplayString()));
            values.add(new StringValue(defaultValue));

            Row row = new Row();
            row.setValues(values);
            rows.add(row);
        }

        return new TableValue(new Table(schemas, rows));
    }
}
