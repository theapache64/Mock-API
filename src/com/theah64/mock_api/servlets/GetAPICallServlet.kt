package com.theah64.mock_api.servlets

import com.theah64.mock_api.database.Routes
import com.theah64.mock_api.models.Param
import com.theah64.mock_api.models.Route
import com.theah64.mock_api.utils.CodeGenJava
import com.theah64.mock_api.utils.Inflector
import com.theah64.mock_api.utils.SlashCutter
import com.theah64.webengine.utils.PathInfo
import com.theah64.webengine.utils.Request
import org.json.JSONException

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import java.sql.SQLException

/**
 * Created by theapache64 on 28/11/17.
 */

@WebServlet(urlPatterns = ["/v1/get_api_call"])
class GetAPICallServlet : AdvancedBaseServlet() {

    override val isSecureServlet: Boolean
        get() = false

    @Throws(javax.servlet.ServletException::class, IOException::class)
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        doPost(req, resp)
    }

    override val requiredParameters: Array<String>?
        get() = arrayOf(KEY_PROJECT_NAME, Routes.COLUMN_NAME)

    @Throws(IOException::class, JSONException::class, SQLException::class, Request.RequestException::class, PathInfo.PathInfoException::class)
    override fun doAdvancedPost() {


        val projectName = getStringParameter(KEY_PROJECT_NAME)!!
        val routeName = getStringParameter(Routes.COLUMN_NAME)!!
        val responseClass = CodeGenJava.getFromFirstCapCharacter(SlashCutter.cut(CodeGenJava.getFirstCharUppercase(CodeGenJava.toCamelCase(routeName)) + "Response"))

        val route = Routes.INSTANCE.get(projectName, routeName)
        if (route != null) {
            httpServletResponse!!.contentType = "text/plain"

            var codeBuilder = StringBuilder()

            //Basic
            codeBuilder.append(String.format("RetrofitClient.getClient().create(APIInterface.class).%s(", SlashCutter.cut(CodeGenJava.toCamelCase(route.name))))

            if (route.isSecure) {
                codeBuilder.append("App.getUser().getApiKey(),")
            }

            //Building param map
            for (param in route.params!!) {
                val paramName = param.name
                codeBuilder.append("\n\t")
                var fCharUp: String? = CodeGenJava.getFirstCharUppercase(CodeGenJava.toCamelCase(paramName))
                if (paramName == "api_key") {
                    codeBuilder.append("App.getCompany().getApiKey(),")
                } else if (paramName.endsWith("_id")) {
                    //Spinner
                    fCharUp = Inflector.getInstance().pluralize(fCharUp!!.substring(0, fCharUp.length - 2))
                    codeBuilder.append("gs").append(fCharUp).append(".getCustomSpinner().getItemSelected().getId(),")
                } else if (paramName.startsWith("is_")) {
                    codeBuilder.append("cb").append(fCharUp).append(".isChecked(),")
                } else if (paramName.contains("date")) {
                    codeBuilder.append("dp").append(fCharUp).append(".getString(),")
                } else {
                    //ValidTextInputLayout
                    codeBuilder.append("vtil").append(fCharUp).append(".getString(),")
                }
            }

            if (!route.params.isEmpty() || route.isSecure) {
                //removing last comma
                val x = codeBuilder.toString().substring(0, codeBuilder.length - 1)
                codeBuilder = StringBuilder(x)
            }


            codeBuilder.append(String.format("\n).enqueue(new CustomRetrofitCallback<BaseAPIResponse<%s>, %s>(this, R.string.Loading){", responseClass, responseClass))
            codeBuilder.append("\n\t").append("@Override")
            codeBuilder.append("\n\t").append(String.format("protected void onSuccess(final String message, %s data) {", responseClass))
            codeBuilder.append("\n\t}")
            codeBuilder.append("\n});")
            /*
            ).enqueue(new CustomRetrofitCallback<BaseAPIResponse<AddCustomerResponse>, AddCustomerResponse>(this, R.string.Adding_customer) {
                    @Override
                    protected void onSuccess(AddCustomerResponse data) {
                        vtilCustomerCode.addSuggestion(data.getCustomer());
                        getResponse().getCustomers().add(0, data.getCustomer());
                        Toast.makeText(CustomerMasterActivity.this, R.string.Added_new_customer, Toast.LENGTH_SHORT).show();
                        refreshPage();
                    }
                });
             */

            writer!!.write(codeBuilder.toString())
        } else {
            throw Request.RequestException("Invalid route")
        }


    }

    private fun getPrimitive(dataType: String): String {
        return if (dataType != "String") {
            dataType.toLowerCase()
        } else dataType
    }

    companion object {

        private val KEY_PROJECT_NAME = "project_name"
    }


}
