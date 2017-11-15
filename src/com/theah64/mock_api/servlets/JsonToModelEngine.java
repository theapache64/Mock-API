package com.theah64.mock_api.servlets;

import com.theah64.mock_api.exceptions.RequestException;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.mock_api.utils.PathInfo;
import com.theah64.mock_api.utils.Request;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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

        final JSONObject joModel = new JSONObject(joString);
        Iterator iterator = joModel.keys();

        List<Model.Property> properties = new ArrayList<>();

        while (iterator.hasNext()) {
            final String variableName = (String) iterator.next();
            final String dataType = getDataType(joModel, variableName);
            properties.add(new Model.Property(dataType, variableName));

        }

        //Sorting
        properties.sort((o1, o2) -> {
            if (o1.variableName.equals("id")) {
                return -1;
            }
            return o1.getVariableName().length() - o2.getVariableName().length();
        });

        final String generatedClassCode = genClassCode(modelName, properties, isRetrofitModel);
        getWriter().write(new APIResponse("Done", "data", generatedClassCode).getResponse());
    }

    private String genClassCode(String modelName, List<Model.Property> properties, boolean isRetrofitModel) {

        final StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(String.format("public class %s {", modelName)).append("\n\n");

        final StringBuilder constructorParams = new StringBuilder();
        final StringBuilder constructorThis = new StringBuilder();
        final StringBuilder getters = new StringBuilder();

        for (final Model.Property property : properties) {
            if (isRetrofitModel) {
                codeBuilder.append(String.format("\t@SerializedName(\"%s\")\n", property.getVariableName()));
            }
            String variableCamelCase = toCamelCase(property.getVariableName());
            final String a = String.format("%s %s", property.getDataType(), variableCamelCase);
            codeBuilder.append(String.format("\tprivate final %s;", a)).append("\n").append(isRetrofitModel ? "\n" : "");

            constructorParams.append(a).append(",");
            constructorThis.append("\n\t\tthis.").append(variableCamelCase).append(" = ").append(variableCamelCase).append(";");

            getters.append("\tpublic ").append(property.getDataType()).append(" ").append(toGetterName(variableCamelCase)).append("{\n\t\treturn ").append(variableCamelCase).append(";\n\t}\n\n");
        }

        codeBuilder.append("\tpublic ").append(modelName).append("(").append(constructorParams.substring(0, constructorParams.length() - 1)).append("){");
        codeBuilder.append(constructorThis);
        codeBuilder.append("\n\t}");

        codeBuilder.append("\n\n").append(getters);

        //class end
        codeBuilder.append("}");

        return codeBuilder.toString().replaceAll("\\n", "<br>").replaceAll("\\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
    }

    private String toGetterName(String input) {
        return "get" + input.substring(0, 1).toUpperCase() + input.substring(1) + "()";
    }

    private static final Pattern p = Pattern.compile("_(.)");

    private String toCamelCase(String string) {

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
