package com.theah64.mock_api.database

import com.sun.istack.internal.Nullable
import com.theah64.mock_api.models.Project
import com.theah64.webengine.database.BaseTable
import com.theah64.webengine.database.Connection
import com.theah64.webengine.database.querybuilders.QueryBuilderException
import com.theah64.webengine.database.querybuilders.UpdateQueryBuilder

import java.sql.PreparedStatement
import java.sql.SQLException

/**
 * Created by theapache64 on 14/5/17.
 */
class Projects private constructor() : BaseTable<Project>("projects") {


    override fun get(column: String, value: String): Project? {
        return get(column, value, null, null)
    }

    override fun update(project: Project): Boolean {
        return UpdateQueryBuilder.Builder(tableName)
                .set(BaseTable.COLUMN_NAME, project.name)
                .set(COLUMN_PACKAGE_NAME, project.packageName)
                .set(COLUMN_BASE_OG_API_URL, project.baseOgApiUrl)
                .set(COLUMN_IS_ALL_SMALL_ROUTES, project.isAllSmallRoutes)
                .set(COLUMN_REQUEST_BODY_TYPE, project.requestBodyType)
                .set(COLUMN_NOTIFICATION_EMAILS, project.notificationEmails)
                .set(COLUMN_DEFAULT_SUCCESS_RESPONSE, project.defaultSuccessResponse)
                .set(COLUMN_DEFAULT_ERROR_RESPONSE, project.defaultErrorResponse)
                .set(COLUMN_BASE_RESPONSE_STRUCTURE, project.baseResponseStructure)
                .where(BaseTable.COLUMN_ID, project.id)
                .build()
                .done()
    }

    override fun get(column1: String, value1: String, column2: String?, value2: String?): Project? {
        var project: Project? = null
        val query: String

        if (column2 != null && value2 != null) {
            query = String.format("SELECT id,name,api_key,request_body_type,is_all_small_routes,notification_emails,package_name,base_og_api_url,pass_hash,default_success_response, default_error_response,base_response_structure FROM %s WHERE %s = ? AND %s = ? AND is_active = 1 LIMIT 1", tableName, column1, column2)
        } else {
            query = String.format("SELECT id,name,api_key,request_body_type,is_all_small_routes,notification_emails,package_name,base_og_api_url,pass_hash,default_success_response, default_error_response,base_response_structure FROM %s WHERE %s = ? AND is_active = 1 LIMIT 1", tableName, column1)
        }
        val resultValue: String? = null
        val con = Connection.getConnection()

        try {
            val ps = con.prepareStatement(query)
            ps.setString(1, value1)
            if (column2 != null && value2 != null) {
                ps.setString(2, value2)
            }


            val rs = ps.executeQuery()

            if (rs.first()) {

                val id = rs.getString(BaseTable.COLUMN_ID)
                val name = rs.getString(BaseTable.COLUMN_NAME)
                val apiKey = rs.getString(COLUMN_API_KEY)
                val passHash = rs.getString(COLUMN_PASS_HASH)
                val packageName = rs.getString(COLUMN_PACKAGE_NAME)
                val requestBodyType = rs.getString(COLUMN_REQUEST_BODY_TYPE)
                val baseOgApiUrl = rs.getString(COLUMN_BASE_OG_API_URL)
                val isAllSmallRoutes = rs.getBoolean(COLUMN_IS_ALL_SMALL_ROUTES)
                val notificationEmails = rs.getString(COLUMN_NOTIFICATION_EMAILS)
                val defaultSuccessResponse = rs.getString(COLUMN_DEFAULT_SUCCESS_RESPONSE)
                val defaultErrorResponse = rs.getString(COLUMN_DEFAULT_ERROR_RESPONSE)
                val baseResponseStructure = rs.getString(COLUMN_BASE_RESPONSE_STRUCTURE)


                project = Project(id, name, passHash, apiKey, requestBodyType, baseOgApiUrl, packageName, isAllSmallRoutes, notificationEmails,
                        defaultSuccessResponse, defaultErrorResponse, baseResponseStructure)
            }

            rs.close()
            ps.close()

        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            try {
                con.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }

        }

        return project
    }

    @Throws(SQLException::class)
    override fun addv3(project: Project): String {
        var error: String? = null
        var id: String? = null
        val query = "INSERT INTO projects (name, pass_hash,api_key,base_og_api_url,package_name,is_all_small_routes,default_success_response, default_error_response,base_response_structure) VALUES (?,?,?,?,?,?,?,?,?);"
        val con = Connection.getConnection()

        try {
            val ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)
            ps.setString(1, project.name!!.replace("\\s+".toRegex(), "").trim { it <= ' ' }.toLowerCase())
            ps.setString(2, project.passHash)
            ps.setString(3, project.apiKey)
            ps.setString(4, project.baseOgApiUrl)
            ps.setString(5, project.packageName)
            ps.setBoolean(6, project.isAllSmallRoutes)
            ps.setString(7, project.defaultSuccessResponse)
            ps.setString(8, project.defaultErrorResponse)
            ps.setString(9, project.baseResponseStructure)
            ps.executeUpdate()
            val rs = ps.generatedKeys

            if (rs.first()) {
                id = rs.getString(1)
            }

            ps.close()
            rs.close()
        } catch (e: SQLException) {
            e.printStackTrace()
            error = e.message
        } finally {
            try {
                con.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }

        }

        BaseTable.manageError(error)
        return id!!
    }


    companion object {

        const val COLUMN_API_KEY = "api_key"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_PASS_HASH = "pass_hash"
        const val COLUMN_BASE_OG_API_URL = "base_og_api_url"
        const val COLUMN_REQUEST_BODY_TYPE = "request_body_type"
        const val COLUMN_PACKAGE_NAME = "package_name"
        const val COLUMN_IS_ALL_SMALL_ROUTES = "is_all_small_routes"
        const val COLUMN_NOTIFICATION_EMAILS = "notification_emails"
        const val COLUMN_DEFAULT_SUCCESS_RESPONSE = "default_success_response"
        const val COLUMN_DEFAULT_ERROR_RESPONSE = "default_error_response"
        const val COLUMN_BASE_RESPONSE_STRUCTURE = "base_response_structure"

        var instance = Projects()
    }


}
