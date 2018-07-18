package com.theah64.mock_api.utils;

import com.theah64.mock_api.models.Model;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by theapache64 on 22/11/17.
 */
public class TypescriptClassGenerator {


    @SuppressWarnings("Duplicates")
    private static void getGenClassCode(final boolean isJsonObject, final StringBuilder codeBuilder, final Object object, final String modelName) throws JSONException {

        JSONObject joModel = null;


        if (object instanceof JSONObject) {

            joModel = (JSONObject) object;

            Iterator iterator = joModel.keys();

            List<Model.Property> properties = new ArrayList<>();

            while (iterator.hasNext()) {

                final String variableName = (String) iterator.next();
                String dataType = GeneratorUtils.getDataType(joModel, variableName);


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


                    getGenClassCode(!isJsonArray, codeBuilder, joModel1, dataType);

                    if (joModel1.getClass().getSimpleName().equals("JSONObject") && !isJsonArray) {
                        dataType = ((joModel1 instanceof JSONArray || joModel1 instanceof JSONObject) ? dataType : joModel1.getClass().getSimpleName());
                    } else {
                        dataType = ((joModel1 instanceof JSONArray || joModel1 instanceof JSONObject) ? GeneratorUtils.removePlural(dataType) : joModel1.getClass().getSimpleName()) + "[]";
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

            codeBuilder.insert(0, genClassCode(modelName, properties, isJsonObject));
        }

    }

    public static String getFinalCode(String joString, String modelName) throws JSONException {
        final StringBuilder codeBuilder = new StringBuilder();

        //It's javascript
        modelName = GeneratorUtils.getFromFirstCapCharacter(SlashCutter.cut(modelName));
        TypescriptClassGenerator.getGenClassCode(true, codeBuilder, new JSONObject(joString), "Data");
        String importStatement = "import { Type, Expose } from 'class-transformer';\nimport { BaseAPIResponse } from './BaseAPIResponse';";
        codeBuilder.insert(0, String.format("%s\n\n/**\n* Generated using MockAPI (https://github.com/theapache64/Mock-API) : %s\n*/\n",
                importStatement,
                new Date().toString()));

        final String responseClassContent =
                "\n  @Type(() => Data)\n" +
                        "  data: Data;\n";

        codeBuilder.append(String.format("export class %s extends BaseAPIResponse {%s}\n", modelName, responseClassContent));

        return codeBuilder.toString();
    }

    private static boolean isPrimitive(final String dataType) {
        switch (dataType) {
            case "string":
            case "number":
                return true;
        }

        return false;
    }

    private static String genClassCode(String modelName, List<Model.Property> properties, boolean isJSONObject) {

        final StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(String.format("class %s {\n", isJSONObject ? modelName : GeneratorUtils.removePlural(modelName))).append("\n");

        for (final Model.Property property : properties) {

            String type = "";
            if (!isPrimitive(property.getDataType())) {
                String dType = property.getDataType();
                if (dType.endsWith("[]")) {
                    dType = dType.substring(0, dType.lastIndexOf("[]"));
                }
                type = String.format("  @Type(() => %s)\n", dType);
            }

            final String expose = String.format("  @Expose({ name: '%s' })\n", property.getVariableName());

            String variableCamelCase = property.getVariableName();
            if (variableCamelCase.contains("_")) {
                variableCamelCase = CodeGenJava.toCamelCase(variableCamelCase);
            } else {
                variableCamelCase = CodeGenJava.getFirstCharSmallcase(variableCamelCase);
            }


            final String a = String.format("readonly %s: %s", variableCamelCase, property.getDataType());
            codeBuilder
                    .append(type)
                    .append(expose)
                    .append(String.format("  %s;\n", a)).append("\n");
        }


        //class end
        codeBuilder.append("}\n\n");


        return codeBuilder.toString();
    }


}
