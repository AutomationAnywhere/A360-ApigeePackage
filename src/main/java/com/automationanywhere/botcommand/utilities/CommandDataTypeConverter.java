/* Copyright (c) 2022 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You shall use it only in
 * accordance with the terms of the license agreement you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand.utilities;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.*;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.botcore.api.dto.AttributeType;
import com.google.gson.internal.LinkedTreeMap;
import java.time.ZonedDateTime;
import java.util.*;

public class CommandDataTypeConverter {
    private static final int DEFAULT_CONVERSION_LEVEL = 1;
    private static final int MAX_CONVERSION_LEVEL = -1;
    private int recursiveInternalLevel;
    private int recursiveConversionLevel;

    public int getRecursiveConversionLevel() {
        return recursiveConversionLevel;
    }

    public void setRecursiveConversionLevel(int recursiveConversionLevel) {
        this.recursiveConversionLevel = recursiveConversionLevel;
    }

    private boolean shouldRecursiveConvert() {
        return this.recursiveInternalLevel < this.getRecursiveConversionLevel()
                || this.recursiveConversionLevel == MAX_CONVERSION_LEVEL;
    }

    public CommandDataTypeConverter() {
        this.resetRecursiveInternalLevel();
        this.recursiveConversionLevel = DEFAULT_CONVERSION_LEVEL;
    }

    public static <T, K> T convertAll(K source, Class<T> targetClass) throws BotCommandException {
        return CommandDataTypeConverter.convert(source, targetClass, MAX_CONVERSION_LEVEL);
    }

    public static <T, K> T convert(K source, Class<T> targetClass) throws BotCommandException {
        return CommandDataTypeConverter.convert(source, targetClass, DEFAULT_CONVERSION_LEVEL);
    }

    public static <T, K> T convert(K source, Class<T> targetClass, int recursiveConversionLevel)
            throws BotCommandException {
        CommandDataTypeConverter converter = new CommandDataTypeConverter();
        converter.setRecursiveConversionLevel(recursiveConversionLevel);
        return converter.convertInternal(source, targetClass, null);
    }

    public <T, K> T convertInternal(K source, Class<T> targetClass, Map<String, Object> options)
            throws BotCommandException {
        try {
            this.recursiveInternalLevel++;

            if (targetClass == AbstractValue.class) {
                return convertToAbstractValue(source, options);
            } else if (targetClass == BooleanValue.class) {
                return convertToBooleanValue(source);
            } else if (targetClass == DateTimeValue.class) {
                return convertToDateTimeValue(source, options);
            } else if (targetClass == DictionaryValue.class) {
                if (source.getClass() == Map.class
                        || source.getClass() == LinkedHashMap.class
                        || source.getClass() == LinkedTreeMap.class
                        || source.getClass() == HashMap.class) {
                    return convertToDictionaryValue(source);
                }
            } else if (targetClass == ListValue.class) {
                if (source.getClass() == List.class || source.getClass() == ArrayList.class) {
                    return convertToListValue(source);
                }
            } else if (targetClass == NumberValue.class) {
                return convertToNumberValue(source, options);
            } else if (targetClass == StringValue.class) {
                return convertToStringValue(source, options);
            } else if (targetClass == RecordValue.class) {
                if (source.getClass() == Map.class
                        || source.getClass() == LinkedHashMap.class
                        || source.getClass() == LinkedTreeMap.class) {
                    return convertToRecordValue(source, options);
                }
            }
        } catch (Exception ex) {
            throw new BotCommandException(
                    "Internal command package error (Error Code: Conversion)", ex);
        } finally {
            this.recursiveInternalLevel--;
        }

        throw new ClassCastException(
                String.format(
                        "This source object type %s is not supported to be converted into %s type.",
                        source.getClass().getName(), targetClass.getName()));
    }

    private <T, K> T convertToRecordValue(K source, Map<String, Object> options) {
        List<Schema> schemas = new ArrayList<>();
        List<Value> data = new ArrayList<>();
        for (Map.Entry<String, Object> entry : ((Map<String, Object>) source).entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Class targetType = toMapSimpleTypeOnly(value.getClass());
            Map option = toSubTypeOption(value.getClass());

            Schema schema = new Schema(key, toAttributeType(value.getClass()));
            schemas.add(schema);

            data.add(convertInternal(value, (Class<Value>) targetType, option));
        }

        return (T) new RecordValue(schemas, data);
    }

    private <T, K> T convertToListValue(K source) {
        List<Value> converted = new ArrayList<Value>();
        for (Object item : (List) source) {
            Class targetType =
                    this.shouldRecursiveConvert()
                            ? toMappedType(item.getClass())
                            : toMapSimpleTypeOnly(item.getClass());
            Map options = this.shouldRecursiveConvert() ? null : toSubTypeOption(item.getClass());

            converted.add(convertInternal(item, (Class<Value>) targetType, options));
        }

        ListValue list = new ListValue();
        list.set(converted);
        return (T) list;
    }

    private <T, K> T convertToDictionaryValue(K source) {
        Map<String, Value> converted = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : ((Map<String, Object>) source).entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null) {
                value = "";
            }
            Class targetType =
                    this.shouldRecursiveConvert()
                            ? toMappedType(value.getClass())
                            : toMapSimpleTypeOnly(value.getClass());
            Map options = this.shouldRecursiveConvert() ? null : toSubTypeOption(value.getClass());

            converted.put(key, convertInternal(value, (Class<Value>) targetType, options));
        }

        return (T) new DictionaryValue(converted);
    }

    private <T, K> T convertToAbstractValue(K source, Map<String, Object> options) {
        return null;
    }

    private <T, K> T convertToBooleanValue(K source) {
        return (T) new BooleanValue(source);
    }

    private <T, K> T convertToNumberValue(K source, Map<String, Object> options) {
        Object improvedNumberSupport = getOption(options, "improvedNumberSupport");
        Boolean support = improvedNumberSupport != null ? (Boolean) improvedNumberSupport : false;
        return (T) new NumberValue(source.toString(), support);
    }

    private <T, K> T convertToDateTimeValue(K source, Map<String, Object> options) {
        if (source.getClass() == String.class || source.getClass() == ZonedDateTime.class) {
            return (T) new DateTimeValue(source);
        } else {
            return (T) new DateTimeValue(source.toString());
        }
    }

    private <T, K> T convertToStringValue(K source, Map<String, Object> options) {
        String sourceValue = null;
        if (source != null) {
            sourceValue = source.toString();
        }

        Object serialize = getOption(options, "serialize");
        if (serialize != null && (Boolean) serialize) {
            sourceValue = JsonSerializer.serialize(source);
        }

        return (T) new StringValue(sourceValue);
    }

    private Object getOption(Map<String, Object> options, String key) {
        if (options != null && options.containsKey(key)) {
            return options.get(key);
        }

        return null;
    }

    private Class toMappedType(Class input) {
        if (input == String.class) {
            return StringValue.class;
        } else if (input == Boolean.class) {
            return BooleanValue.class;
        } else if (input == Integer.class || input == Double.class || input == Float.class) {
            return NumberValue.class;
        } else if (input == Date.class || input == ZonedDateTime.class) {
            return DateTimeValue.class;
        } else if (input == List.class || input == ArrayList.class) {
            return ListValue.class;
        } else if (input == Map.class
                || input == LinkedHashMap.class
                || input == HashMap.class
                || input == LinkedTreeMap.class) {
            return DictionaryValue.class;
        } else {
            throw new ClassCastException(
                    String.format(
                            "This type %s doesn't have a mapping object type.", input.getName()));
        }
    }

    private Class toMapSimpleTypeOnly(Class input) {
        try {
            Class outputType = toMappedType(input);
            if (outputType == ListValue.class
                    || outputType == DictionaryValue.class
                    || outputType == LinkedTreeMap.class
                    || outputType == ArrayList.class) {
                return StringValue.class;
            }
            return outputType;
        } catch (ClassCastException e) {
            return StringValue.class;
        }
    }

    private Map toSubTypeOption(Class input) {
        try {
            Class outputType = toMappedType(input);
            if (outputType == ListValue.class || outputType == DictionaryValue.class) {
                return new HashMap<>() {
                    {
                        put("serialize", true);
                    }
                };
            }
            return null;
        } catch (ClassCastException e) {
            return null;
        }
    }

    private void resetRecursiveInternalLevel() {
        this.recursiveInternalLevel = 0;
    }

    private AttributeType toAttributeType(Class input) {
        if (input == String.class
                || input == List.class
                || input == ArrayList.class
                || input == Map.class
                || input == LinkedHashMap.class
                || input == HashMap.class
                || input == LinkedTreeMap.class) {
            return AttributeType.STRING;
        } else if (input == Boolean.class) {
            return AttributeType.BOOLEAN;
        } else if (input == Integer.class || input == Double.class || input == Float.class) {
            return AttributeType.NUMBER;
        } else if (input == Date.class || input == ZonedDateTime.class) {
            return AttributeType.DATETIME;
        } else {
            return AttributeType.ANY;
        }
    }
}
