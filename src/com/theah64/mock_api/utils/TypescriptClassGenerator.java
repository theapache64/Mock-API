package com.theah64.mock_api.utils;

import com.theah64.mock_api.models.Model;
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
                    dataType = GeneratorUtils.underScoreMagic(variableName,dataType);


                    getGenClassCode(!isJsonArray, codeBuilder, joModel1, dataType);

                    if (joModel1.getClass().getSimpleName().equals("JSONObject") && !isJsonArray) {
                        dataType = ((joModel1 instanceof JSONArray || joModel1 instanceof JSONObject) ? dataType : joModel1.getClass().getSimpleName());
                    } else {
                        dataType = "Array&#60;" + ((joModel1 instanceof JSONArray || joModel1 instanceof JSONObject) ? GeneratorUtils.removePlural(dataType) : joModel1.getClass().getSimpleName()) + "&#62;";
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
        codeBuilder.insert(0, String.format("%s\n\n/**\n* Generated using MockAPI (https://github.com/theapache64/Mock-API) : %s\n*/\n",
                "import BaseAPIResponse from './BaseAPIResponse';",
                new Date().toString()));

        codeBuilder.append(String.format("export default class %s extends BaseAPIResponse<Data> {}", modelName));

        return codeBuilder.toString();
    }

    private static String genClassCode(String modelName, List<Model.Property> properties, boolean isJSONObject) {

        final StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(String.format("class %s {", isJSONObject ? modelName : GeneratorUtils.removePlural(modelName))).append("\n");
        codeBuilder.append("\n\tconstructor(\n");
        for (final Model.Property property : properties) {


            String variableCamelCase = property.getVariableName();
            final String a = String.format("\tpublic readonly %s: %s", variableCamelCase, property.getDataType());
            codeBuilder.append(String.format("\t%s,", a)).append("\n");
        }


        codeBuilder.append("\t){}\n");

        //class end
        codeBuilder.append("}\n\n");


        return codeBuilder.toString();
    }



}
