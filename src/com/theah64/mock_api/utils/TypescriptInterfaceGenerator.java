package com.theah64.mock_api.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by theapache64 on 22/11/17.
 */
public class TypescriptInterfaceGenerator {


    private static final String SERIALIZED_NAME_IMPORT = "import com.google.gson.annotations.SerializedName;";

    private static void getGenClassCode(final boolean isJsonObject, final StringBuilder codeBuilder, final Object object, final String modelName) throws JSONException {

        JSONObject joModel = null;


        if (object instanceof JSONObject) {

            joModel = (JSONObject) object;

            Iterator iterator = joModel.keys();

            List<Model.Property> properties = new ArrayList<>();

            while (iterator.hasNext()) {

                final String variableName = (String) iterator.next();
                String dataType = getDataType(joModel, variableName);


                if (dataType.equals("JSONArray") || dataType.equals("JSONObject")) {

                    Object joModel1 = null;
                    if (dataType.equals("JSONArray") && joModel.getJSONArray(variableName).length() == 0) {
                        joModel1 = new JSONObject();
                    } else {
                        joModel1 = dataType.equals("JSONArray") ? joModel.getJSONArray(variableName).get(0) : joModel.getJSONObject(variableName);
                    }

                    final boolean isJsonArray = dataType.equals("JSONArray");

                    //Capital first letter
                    dataType = variableName.substring(0, 1).toUpperCase() + variableName.substring(1);

                    if (dataType.contains("_")) {
                        final String[] chunks = dataType.split("_");
                        final StringBuilder sb = new StringBuilder();
                        for (final String chunk : chunks) {
                            sb.append(chunk.substring(0, 1).toUpperCase()).append(chunk.substring(1));
                        }
                        dataType = sb.toString();
                    }


                    getGenClassCode(!isJsonArray, codeBuilder, joModel1, dataType);

                    if (joModel1.getClass().getSimpleName().equals("JSONObject") && !isJsonArray) {
                        dataType = ((joModel1 instanceof JSONArray || joModel1 instanceof JSONObject) ? dataType : joModel1.getClass().getSimpleName());
                    } else {
                        dataType = "Array&#60;" + ((joModel1 instanceof JSONArray || joModel1 instanceof JSONObject) ? removePlural(dataType) : joModel1.getClass().getSimpleName()) + "&#62;";
                    }
                }


                properties.add(new Model.Property(dataType, variableName));
            }

            //Sorting
            properties.sort((o1, o2) -> {
                if (o1.variableName.equals("id")) {
                    return -1;
                }

                return o1.getVariableName().length() - o2.getVariableName().length();
            });

            codeBuilder.insert(0, genClassCode(true, modelName, properties, isJsonObject));
        }

    }

    public static String getFinalCode(String joString, String modelName) throws JSONException {
        final StringBuilder codeBuilder = new StringBuilder();

        //It's javascript
        modelName = TypescriptInterfaceGenerator.getFromFirstCapCharacter(SlashCutter.cut(modelName));
        TypescriptInterfaceGenerator.getGenClassCode(true, codeBuilder, new JSONObject(joString), "Data");
        codeBuilder.insert(0, String.format("%s\n\n%s\n\n/**\n* Generated using MockAPI (https://github.com/theapache64/Mock-API) : %s\n*/\n",
                "// @flow",
                "import BaseAPIResponse from './BaseAPIResponse';",
                new Date().toString()));

        codeBuilder.append(String.format("export default interface %s extends BaseAPIResponse<Data> {}",modelName));

        return codeBuilder.toString();
    }

    private static String genClassCode(boolean isNestedClass, String modelName, List<Model.Property> properties, boolean isJSONObject) {

        final StringBuilder codeBuilder = new StringBuilder();
        if (isNestedClass) {
            codeBuilder.append(String.format("interface %s {", isJSONObject ? modelName : removePlural(modelName))).append("\n");
        }

        //final StringBuilder constructorParams = new StringBuilder();
        //final StringBuilder constructorThis = new StringBuilder();
        //final StringBuilder getters = new StringBuilder();

        for (final Model.Property property : properties) {


            String variableCamelCase = property.getVariableName();
            final String a = String.format("readonly %s: %s",variableCamelCase,property.getDataType());
            codeBuilder.append(String.format("%s%s;", isNestedClass ? "\t" : "", a)).append("\n");
        }

        //class end
        if (isNestedClass) {
            codeBuilder.append("}\n\n");
        }


        return codeBuilder.toString();
    }

    private static String removePlural(String modelName) {
        if (modelName.equals("Data")) {
            return modelName;
        }
        return Inflector.getInstance().singularize(modelName);
    }

    public static String toGetterName(String dataType, String input) {
        return (dataType.equals("boolean") ? "is" : "get") + getFirstCharUppercase(input) + "()";
    }

    private static String getFirstCharUppercase(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    private static final Pattern p = Pattern.compile("_(.)");

    private static String getDataType(final JSONObject joModel, String variableName) throws JSONException {

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

    private static String getFromFirstCapCharacter(String string) {
        int pos = 0;
        for (int i = 0; i < string.length(); i++) {
            if (Character.isUpperCase(string.charAt(i))) {
                pos = i;
                break;
            }
        }
        return string.substring(pos);
    }

    static class Model {
        private final String name;
        private final List<Property> properties;

        Model(String name, List<Property> properties) {
            this.name = name;
            this.properties = properties;
        }

        public String getName() {
            return name;
        }

        public List<Property> getProperties() {
            return properties;
        }

        static class Property {
            private final String dataType, variableName;

            Property(String dataType, String variableName) {
                this.dataType = dataType;
                this.variableName = variableName;
            }

            String getDataType() {
                return dataType;
            }

            String getVariableName() {
                return variableName;
            }
        }
    }
}
