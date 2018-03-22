package com.theah64.mock_api.utils;

import com.theah64.mock_api.database.Routes;
import com.theah64.mock_api.models.Param;
import com.theah64.mock_api.models.Route;
import com.theah64.webengine.utils.Request;

import java.sql.SQLException;

/*
    private static final String KEY_CODE = "code";
    private static final String KEY_FROM_DATE = "from_date";
    private static final String KEY_TO_DATE = "to_date";
    private static final String KEY_TYPE = "type";




    public static void start(Context context, String pageTitle, String code, String fromDate, String toDate, String type, String[] ghostKeys) {
        final Intent i = new Intent(context, GeneralLedgerReportResultActivity.class);
        i.putExtra(KEY_GHOST_KEYS, ghostKeys);
        i.putExtra(KEY_CODE, code);
        i.putExtra(KEY_FROM_DATE, fromDate);
        i.putExtra(KEY_TO_DATE, toDate);
        i.putExtra(KEY_TYPE, type);
        context.startActivity(i);
    }

    @Override
    protected Call<BaseAPIResponse<GeneralLedgerReportResponse>> getCall(APIInterface apiInterface) {
        return apiInterface.getGeneralLedgerReport(
                App.getCompany().getApiKey(),
                getString(KEY_CODE),
                getString(KEY_FROM_DATE),
                getString(KEY_TO_DATE),
                getString(KEY_TYPE)
        );
    }

*/
public class ActivityCodeGen {

    public static final String KEY_ROUTE_NAME = "route_name";
    public static final String KEY_PROJECT_NAME = "project_name";

    public static ActivityCode generate(String projectName, String routeName) throws SQLException, Request.RequestException {
        final Route route = Routes.getInstance().get(projectName, routeName);

        if (route == null) {
            throw new Request.RequestException("No route found : " + projectName + ":" + routeName);
        }

        //First build constants needed
        final StringBuilder constantsBuilder = new StringBuilder();
        final StringBuilder activityCallSignatureBuilder = new StringBuilder("public static void start(Context context");
        final StringBuilder activityCallBodyBuilder = new StringBuilder("\n\tfinal Intent i = new Intent(context, Activity.class);");
        final StringBuilder apiCall = new StringBuilder();
        apiCall.append("@Override");
        final String entityName = CodeGen.getFirstCharUppercase(CodeGen.toCamelCase(routeName));
        apiCall.append(String.format("\nprotected Call<BaseAPIResponse<%sResponse>> getCall(APIInterface apiInterface) {", SlashCutter.cut(entityName)));
        apiCall.append(String.format("\n\treturn apiInterface.%s(", SlashCutter.cut(CodeGen.toCamelCase(route.getName()))));

        for (final Param param : route.getParams()) {

            final String constName = String.format("KEY_%s", toConstant(param.getName()));
            constantsBuilder.append(String.format("private static final String %s = \"%s\";", constName, param.getName())).append("\n");
            final String camelCase = CodeGen.toCamelCase(param.getName());
            activityCallSignatureBuilder.append(String.format(", %s String ", param.isRequired() ? "@NonNull" : "@Nullable")).append(camelCase);
            activityCallBodyBuilder.append(String.format("\n\ti.putExtra(%s, %s);", constName, camelCase));
            apiCall.append(String.format("\n\t\t%s(%s),", param.isRequired() ? "getStringOrThrow" : "getString", constName));
        }

        String apiCallString = apiCall.toString();
        if (apiCallString.endsWith(",")) {
            apiCallString = apiCallString.substring(0, apiCallString.length() - 1) + "\n\t);\n}";
        }

        activityCallSignatureBuilder.append("){");
        activityCallBodyBuilder.append("\n\tcontext.startActivity(i);\n}");

        return new ActivityCode(constantsBuilder.toString(), apiCallString, String.valueOf(activityCallSignatureBuilder) + activityCallBodyBuilder);
    }

    private static String toConstant(String name) {
        return name.toUpperCase();
    }


    public static class ActivityCode {

        private final String constants, apiCall, activityCall;

        public ActivityCode(String constants, String apiCall, String activityCall) {
            this.constants = constants;
            this.apiCall = apiCall;
            this.activityCall = activityCall;
        }


        public String getConstants() {
            return constants;
        }

        public String getApiCall() {
            return apiCall;
        }

        public String getActivityCall() {
            return activityCall;
        }

        @Override
        public String toString() {
            return constants + "\n\n" + activityCall + "\n\n" + apiCall;
        }
    }
}
