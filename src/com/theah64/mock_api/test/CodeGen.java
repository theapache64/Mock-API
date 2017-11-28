package com.theah64.mock_api.test;

import com.theah64.mock_api.utils.Inflector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by theapache64 on 22/11/17.
 */
public class CodeGen {


    public static void getGenClassCode(final boolean isNestedClass, final StringBuilder codeBuilder, final Object object, final String modelName, final boolean isRetrofitModel) throws JSONException {

        JSONObject joModel = null;

        if (object instanceof JSONObject) {

            joModel = (JSONObject) object;

            Iterator iterator = joModel.keys();

            List<Model.Property> properties = new ArrayList<>();

            while (iterator.hasNext()) {

                final String variableName = (String) iterator.next();
                String dataType = getDataType(joModel, variableName);
                if (dataType.equals("JSONArray") || dataType.equals("JSONObject")) {

                    final Object joModel1 = dataType.equals("JSONArray") ? joModel.getJSONArray(variableName).get(0) : joModel.getJSONObject(variableName);
                    final boolean x = dataType.equals("JSONArray");

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


                    getGenClassCode(true, codeBuilder, joModel1, dataType, isRetrofitModel);

                    if (joModel1.getClass().getSimpleName().equals("JSONObject") && !x) {
                        dataType = ((joModel1 instanceof JSONArray || joModel1 instanceof JSONObject) ? removePlural(dataType) : joModel1.getClass().getSimpleName());
                    } else {
                        dataType = "List&#60;" + ((joModel1 instanceof JSONArray || joModel1 instanceof JSONObject) ? removePlural(dataType) : joModel1.getClass().getSimpleName()) + "&#62;";
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

            codeBuilder.insert(0, genClassCode(isNestedClass, modelName, properties, isRetrofitModel));

        }


    }

    public static String getFinalCode(String joString, String modelName, boolean isRetrofitModel) throws JSONException {
        final StringBuilder codeBuilder = new StringBuilder();
        CodeGen.getGenClassCode(false, codeBuilder, new JSONObject(joString), modelName, isRetrofitModel);
        codeBuilder.insert(0, String.format("/**\n* Generated using MockAPI (https://github.com/theapache64/Mock-API) : %s\n*/ \npublic class %s {\n\n", new Date().toString(), modelName));
        codeBuilder.append("\n\n}");

        return codeBuilder.toString().replaceAll("\\n", "<br>").replaceAll("\\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
    }

    private static String genClassCode(boolean isNestedClass, String modelName, List<Model.Property> properties, boolean isRetrofitModel) {

        final StringBuilder codeBuilder = new StringBuilder();
        if (isNestedClass) {
            codeBuilder.append(String.format("\tpublic static class %s {", removePlural(modelName))).append("\n\n");
        }

        final StringBuilder constructorParams = new StringBuilder();
        final StringBuilder constructorThis = new StringBuilder();
        final StringBuilder getters = new StringBuilder();

        for (final Model.Property property : properties) {

            if (isRetrofitModel) {
                codeBuilder.append(String.format("%s\t@SerializedName(\"%s\")\n", isNestedClass ? "\t" : "", property.getVariableName()));
            }
            String variableCamelCase = toCamelCase(property.getVariableName());
            final String a = String.format("%s %s", property.getDataType(), variableCamelCase);
            codeBuilder.append(String.format("%s\tprivate final %s;", isNestedClass ? "\t" : "", a)).append("\n").append(isRetrofitModel ? "\n" : "");

            constructorParams.append(a).append(",");
            constructorThis.append(String.format("\n%s\t\tthis.", isNestedClass ? "\t" : "")).append(variableCamelCase).append(" = ").append(variableCamelCase).append(";");


            getters.append(String.format("%s\tpublic ", isNestedClass ? "\t" : "")).append(property.getDataType()).append(" ").append(toGetterName(variableCamelCase)).append(String.format("{\n%s\t\treturn ", isNestedClass ? "\t" : "")).append(variableCamelCase).append(String.format(";\n%s\t}\n\n", isNestedClass ? "\t" : ""));
        }

        codeBuilder.append(String.format("\n%s\tpublic ", isNestedClass ? "\t" : "")).append(removePlural(modelName)).append("(").append(constructorParams.substring(0, constructorParams.length() - 1)).append("){");
        codeBuilder.append(constructorThis);
        codeBuilder.append(String.format("\n%s\t}", isNestedClass ? "\t" : ""));

        codeBuilder.append("\n\n").append(getters);

        //class end
        if (isNestedClass) {
            codeBuilder.append("\t}\n\n");
        }


        return codeBuilder.toString();
    }

    private static String removePlural(String modelName) {
        return Inflector.getInstance().singularize(modelName);
    }

    private static String toGetterName(String input) {
        return "get" + getFirstCharUppercase(input) + "()";
    }

    public static String getFirstCharUppercase(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    private static final Pattern p = Pattern.compile("_(.)");

    public static String toCamelCase(String string) {
        string = string.toLowerCase();
        Matcher m = p.matcher(string);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, m.group(1).toUpperCase());
        }
        m.appendTail(sb);

        return sb.toString();
    }

    private static String getDataType(final JSONObject joModel, String variableName) throws JSONException {

        final Object data = joModel.get(variableName);

        if (data instanceof Integer) {
            return "int";
        } else if (data instanceof Double) {
            return "double";
        } else if (data instanceof String) {
            return "String";
        } else {
            return data.getClass().getSimpleName();
        }

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

            public String getDataType() {
                return dataType;
            }

            public String getVariableName() {
                return variableName;
            }
        }
    }
}
