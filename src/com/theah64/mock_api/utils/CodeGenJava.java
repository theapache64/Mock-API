package com.theah64.mock_api.utils;

import com.theah64.mock_api.models.Model;
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
public class CodeGenJava {


    private static final String SERIALIZED_NAME_IMPORT = "import com.google.gson.annotations.SerializedName;";
    private static final Pattern p = Pattern.compile("_(.)");
    private static boolean hasList = false;

    private static boolean getGenClassCode(final boolean isJsonObject, final boolean isNestedClass, final StringBuilder codeBuilder, final Object object, final String modelName, final boolean isRetrofitModel) throws JSONException {

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
                    dataType = GeneratorUtils.underScoreMagic(variableName, dataType);


                    getGenClassCode(!isJsonArray, true, codeBuilder, joModel1, dataType, isRetrofitModel);

                    if (joModel1.getClass().getSimpleName().equals("JSONObject") && !isJsonArray) {
                        dataType = ((joModel1 instanceof JSONArray || joModel1 instanceof JSONObject) ? dataType : joModel1.getClass().getSimpleName());
                    } else {
                        dataType = "List&#60;" + ((joModel1 instanceof JSONArray || joModel1 instanceof JSONObject) ? removePlural(dataType) : joModel1.getClass().getSimpleName()) + "&#62;";
                        hasList = true;
                    }
                }


                properties.add(new Model.Property(dataType, variableName));
            }

            //Sorting
            properties.sort((o1, o2) -> {
                if (o1.getVariableName().equals("id")) {
                    return -1;
                }

                return o1.getVariableName().length() - o2.getVariableName().length();
            });

            codeBuilder.insert(0, genClassCode(isNestedClass, modelName, properties, isRetrofitModel, isJsonObject));
        }

        return hasList;

    }

    public static String getFinalCode(final String packageName, String joString, String modelName, boolean isRetrofitModel) throws JSONException {
        final StringBuilder codeBuilder = new StringBuilder();
        hasList = false;
        modelName = CodeGenJava.getFromFirstCapCharacter(SlashCutter.cut(modelName));
        CodeGenJava.getGenClassCode(true, false, codeBuilder, new JSONObject(joString), modelName, isRetrofitModel);
        codeBuilder.insert(0, String.format("%s\n\n%s\n%s\n\n/**\n* Generated using MockAPI (https://github.com/theapache64/Mock-API) : %s\n*/ \npublic class %s {\n\n",
                "package " + packageName + ".api.responses;",
                isRetrofitModel ? SERIALIZED_NAME_IMPORT : "",
                hasList ? "import java.util.List;" : "",
                new Date().toString(), modelName));
        codeBuilder.append("\n\n}");
        return codeBuilder.toString();
    }

    private static String genClassCode(boolean isNestedClass, String modelName, List<Model.Property> properties, boolean isRetrofitModel, boolean isJSONObject) {

        final StringBuilder codeBuilder = new StringBuilder();
        if (isNestedClass) {
            codeBuilder.append(String.format("\tpublic static class %s {", isJSONObject ? modelName : removePlural(modelName))).append("\n\n");
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


            getters.append(String.format("%s\tpublic ", isNestedClass ? "\t" : "")).append(property.getDataType()).append(" ").append(toGetterName(property.getDataType(), variableCamelCase)).append(String.format("{\n%s\t\treturn ", isNestedClass ? "\t" : "")).append(variableCamelCase).append(String.format(";\n%s\t}\n\n", isNestedClass ? "\t" : ""));
        }

        codeBuilder.append(String.format("\n%s\tpublic ", isNestedClass ? "\t" : "")).append(isJSONObject ? modelName : removePlural(modelName)).append("(")
                .append(constructorParams.substring(0, constructorParams.length() > 0 ? constructorParams.length() - 1 : 0)).append("){");
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
        if (modelName.equals("Data")) {
            return modelName;
        }
        return Inflector.getInstance().singularize(modelName);
    }

    public static String toGetterName(String dataType, String input) {
        return (dataType.equals("boolean") ? "is" : "get") + getFirstCharUppercase(input) + "()";
    }

    public static String getFirstCharUppercase(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static String getFirstCharSmallcase(String input) {
        return input.substring(0, 1).toLowerCase() + input.substring(1);
    }

    public static String toCamelCase(String string) {
        string = string.toLowerCase();
        Matcher m = p.matcher(string);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, m.group(1).toUpperCase());
        }
        m.appendTail(sb);

        //Camelcasing letters after special chars


        return sb.toString();
    }


    private static String getDataType(final JSONObject joModel, String variableName) throws JSONException {

        final Object data = joModel.get(variableName);
        if (data instanceof Integer) {
            return "int";
        } else if (data instanceof Double) {
            return "double";
        } else if (data instanceof String || data.toString().equals("null")) {
            return "String";
        } else if (data instanceof Boolean) {
            return "boolean";
        } else {
            return data.getClass().getSimpleName();
        }

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


}
