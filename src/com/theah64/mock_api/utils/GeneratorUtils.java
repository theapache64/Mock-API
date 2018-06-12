package com.theah64.mock_api.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class GeneratorUtils {
    public static String getDataType(final JSONObject joModel, String variableName) throws JSONException {

        final Object data = joModel.get(variableName);
        if (data instanceof Integer) {
            return "number";
        } else if (data instanceof Double) {
            return "number";
        } else if (data instanceof String || data.toString().equals("null")) {
            return "string";
        } else if (data instanceof Boolean) {
            return "boolean";
        } else {
            return data.getClass().getSimpleName();
        }
    }

    public static String removePlural(String modelName) {
        if (modelName.equals("Data")) {
            return modelName;
        }
        return Inflector.getInstance().singularize(modelName);
    }


    public static String getFromFirstCapCharacter(String string) {
        int pos = 0;
        for (int i = 0; i < string.length(); i++) {
            if (Character.isUpperCase(string.charAt(i))) {
                pos = i;
                break;
            }
        }
        return string.substring(pos);
    }

    public static String underScoreMagic(String variableName, String dataType) {

        dataType = variableName.substring(0, 1).toUpperCase() + variableName.substring(1);

        if (dataType.contains("_")) {
            final String[] chunks = dataType.split("_");
            final StringBuilder sb = new StringBuilder();
            for (final String chunk : chunks) {
                sb.append(chunk.substring(0, 1).toUpperCase()).append(chunk.substring(1));
            }
            dataType = sb.toString();
        }

        return dataType;
    }
}
