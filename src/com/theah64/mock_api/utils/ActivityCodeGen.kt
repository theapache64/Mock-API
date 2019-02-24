package com.theah64.mock_api.utils

import com.theah64.mock_api.database.Routes
import com.theah64.mock_api.models.Param
import com.theah64.mock_api.models.Route

import com.theah64.webengine.utils.Request

import java.sql.SQLException

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
object ActivityCodeGen {

    val KEY_ROUTE_NAME = "route_name"
    val KEY_PROJECT_NAME = "project_name"

    @Throws(SQLException::class, Request.RequestException::class)
    fun generate(projectName: String, routeName: String): ActivityCode {
        val route = Routes.INSTANCE.get(projectName, routeName)
                ?: throw Request.RequestException("No route found : $projectName:$routeName")

        // First build constants needed
        val constantsBuilder = StringBuilder()
        val activityCallSignatureBuilder = StringBuilder("public static void start(Context context")
        val activityCallBodyBuilder = StringBuilder("\n\tfinal Intent i = new Intent(context, Activity.class);")
        val apiCall = StringBuilder()
        apiCall.append("@Override")
        val entityName = CodeGenJava.getFirstCharUppercase(CodeGenJava.toCamelCase(routeName))
        apiCall.append(String.format("\nprotected Call<BaseAPIResponse<%sResponse>> getCall(APIInterface apiInterface) {", SlashCutter.cut(entityName)))
        apiCall.append(String.format("\n\treturn apiInterface.%s(", SlashCutter.cut(CodeGenJava.toCamelCase(route.name))))

        for (param in route.params!!) {

            val constName = String.format("KEY_%s", toConstant(param.name))
            constantsBuilder.append(String.format("private static final String %s = \"%s\";", constName, param.name)).append("\n")
            val camelCase = CodeGenJava.toCamelCase(param.name)
            activityCallSignatureBuilder.append(String.format(", %s String ", if (param.isRequired) "@NonNull" else "@Nullable")).append(camelCase)
            activityCallBodyBuilder.append(String.format("\n\ti.putExtra(%s, %s);", constName, camelCase))
            apiCall.append(String.format("\n\t\t%s(%s),", if (param.isRequired) "getStringOrThrow" else "getString", constName))
        }

        var apiCallString = apiCall.toString()
        if (apiCallString.endsWith(",")) {
            apiCallString = apiCallString.substring(0, apiCallString.length - 1) + "\n\t);\n}"
        }

        activityCallSignatureBuilder.append("){")
        activityCallBodyBuilder.append("\n\tcontext.startActivity(i);\n}")

        return ActivityCode(constantsBuilder.toString(), apiCallString, activityCallSignatureBuilder.toString() + activityCallBodyBuilder)
    }

    private fun toConstant(name: String): String {
        return name.toUpperCase()
    }


    class ActivityCode(val constants: String, val apiCall: String, val activityCall: String) {

        override fun toString(): String {
            return constants + "\n\n" + activityCall + "\n\n" + apiCall
        }
    }
}
