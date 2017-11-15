package com.theah64.mock_api.servlets;

import com.theah64.mock_api.exceptions.RequestException;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.mock_api.utils.Inflector;
import com.theah64.mock_api.utils.PathInfo;
import com.theah64.mock_api.utils.Request;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by theapache64 on 13/11/17.
 */
@WebServlet(AdvancedBaseServlet.VERSION_CODE + "/json_to_model_engine")
public class JsonToModelEngine extends AdvancedBaseServlet {

    private static final String KEY_MODEL_NAME = "model_name";
    private static final String KEY_JO_STRING = "jo_string";
    private static final String KEY_IS_RETROFIT_MODEL = "is_retrofit_model";

    @Override
    protected boolean isSecureServlet() {
        return false;
    }

    @Override
    protected String[] getRequiredParameters() throws Request.RequestException {
        return new String[]{
                KEY_MODEL_NAME,
                KEY_JO_STRING,
                KEY_IS_RETROFIT_MODEL
        };
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException, RequestException, PathInfo.PathInfoException {

        final String modelName = getStringParameter(KEY_MODEL_NAME);
        final String joString = getStringParameter(KEY_JO_STRING);
        final boolean isRetrofitModel = getBooleanParameter(KEY_IS_RETROFIT_MODEL);


        final StringBuilder codeBuilder = new StringBuilder();
        getGenClassCode(false, codeBuilder, new JSONObject(joString), modelName, isRetrofitModel);

        codeBuilder.insert(0, String.format("/**\n* Generated using MockAPI (https://github.com/theapache64/Mock-API) : %s\n*/ \npublic class %s {\n\n", new Date().toString(), modelName));
        codeBuilder.append("\n\n}");

        getWriter().write(new APIResponse("Done", "data", codeBuilder.toString().replaceAll("\\n", "<br>").replaceAll("\\t", "&nbsp;&nbsp;&nbsp;&nbsp;")).getResponse());
    }

    private String genClassCode(boolean isNestedClass, String modelName, List<Model.Property> properties, boolean isRetrofitModel) {

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


            getters.append(String.format("%s\tpublic ", isNestedClass ? "\t" : "")).append(property.getDataType()).append(" ").append(toGetterName(variableCamelCase)).append("{\n\t\treturn ").append(variableCamelCase).append(String.format(";\n%s\t}\n\n", isNestedClass ? "\t" : ""));
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

    private String removePlural(String modelName) {
        return Inflector.getInstance().singularize(modelName);
    }

    private String toGetterName(String input) {
        return "get" + input.substring(0, 1).toUpperCase() + input.substring(1) + "()";
    }

    private static final Pattern p = Pattern.compile("_(.)");

    private String toCamelCase(String string) {
        string = string.toLowerCase();
        Matcher m = p.matcher(string);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, m.group(1).toUpperCase());
        }
        m.appendTail(sb);

        return sb.toString();
    }

    private String getDataType(final JSONObject joModel, String variableName) throws JSONException {

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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
        super.doPost(req, resp);
    }

    public void getGenClassCode(final boolean isNestedClass, final StringBuilder codeBuilder, final Object object, final String modelName, final boolean isRetrofitModel) throws JSONException {

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
                    dataType = "List&#60;" + ((joModel1 instanceof JSONArray || joModel1 instanceof JSONObject) ? removePlural(dataType) : joModel1.getClass().getSimpleName()) + "&#62;";
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
