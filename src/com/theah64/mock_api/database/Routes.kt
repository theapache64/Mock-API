package com.theah64.mock_api.database

import com.theah64.mock_api.models.Route
import com.theah64.webengine.database.BaseTable
import com.theah64.webengine.database.Connection
import com.theah64.webengine.database.querybuilders.QueryBuilderException

import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.ArrayList

/**
 * Created by theapache64 on 14/5/17.
 */
class Routes private constructor() : BaseTable<Route>("routes") {

    @Throws(SQLException::class)
    override fun addv3(route: Route): String {
        var error: String? = null
        var id: String? = null
        val query = "INSERT INTO routes (project_id, name, default_response, description, is_secure, delay,external_api_url,updated_at_in_millis,method,request_body_type,json_req_body) VALUES (?,?,?,?,?,?,?,?,?,?,?);"
        val con = Connection.getConnection()
        try {
            val ps0 = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)
            ps0.setString(1, route.projectId)
            ps0.setString(2, route.name)
            ps0.setString(3, route.defaultResponse)
            ps0.setString(4, route.description)
            ps0.setBoolean(5, route.isSecure)
            ps0.setLong(6, route.delay)
            ps0.setString(7, route.externalApiUrl)
            ps0.setLong(8, System.currentTimeMillis())
            ps0.setString(9, route.method)
            ps0.setString(10, route.requestBodyType)
            ps0.setString(11, route.jsonReqBody)

            ps0.executeUpdate()
            val rs = ps0.generatedKeys
            if (rs.first()) {
                id = rs.getString(1)
            }
            rs.close()
            ps0.close()

            route.id = id
            Params.instance.addParamsFromRoute(route)

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

    @Throws(SQLException::class)
    fun getAll(projectId: String): List<Route> {

        val jsonList = ArrayList<Route>()
        val query = "SELECT id, name,request_body_type, external_api_url FROM routes WHERE project_id = ? AND is_active = 1 ORDER BY id DESC"
        var error: String? = null
        val con = Connection.getConnection()
        try {
            val ps = con.prepareStatement(query)
            ps.setString(1, projectId)
            val rs = ps.executeQuery()

            if (rs.first()) {

                do {

                    val id = rs.getString(BaseTable.COLUMN_ID)
                    val route = rs.getString(BaseTable.COLUMN_NAME)
                    val requestBodyType = rs.getString(COLUMN_REQUEST_BODY_TYPE)
                    val externalApiUrl = rs.getString(COLUMN_EXTERNAL_API_URL)

                    jsonList.add(Route(id, projectId, route, requestBodyType, null, null, null, externalApiUrl, null, null, false, 0, -1))

                } while (rs.next())
            }
            rs.close()
            ps.close()
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

        return jsonList

    }

    @Throws(SQLException::class)
    fun getAllDetailed(projectId: String): List<Route> {

        var routeList: MutableList<Route> = ArrayList()

        val query = "SELECT r.id,r.name,r.request_body_type,r.json_req_body, r.updated_at_in_millis,r.method, r.description, r.is_secure, r.delay, r.default_response, external_api_url FROM routes r INNER JOIN projects p ON p.id = r.project_id WHERE p.id = ? AND p.is_active = 1 AND r.is_active = 1 GROUP BY r.id;"
        var error: String? = null
        val con = Connection.getConnection()
        try {
            val ps = con.prepareStatement(query)
            ps.setString(1, projectId)
            val rs = ps.executeQuery()

            if (rs.first()) {

                routeList = ArrayList()

                do {

                    val id = rs.getString(BaseTable.COLUMN_ID)
                    val routeName = rs.getString(BaseTable.COLUMN_NAME)
                    val response = rs.getString(COLUMN_DEFAULT_RESPONSE)
                    val description = rs.getString(COLUMN_DESCRIPTION)
                    val jsonReqBody = rs.getString(COLUMN_JSON_REQ_BODY)
                    val isSecure = rs.getBoolean(COLUMN_IS_SECURE)
                    val requestBodyType = rs.getString(COLUMN_REQUEST_BODY_TYPE)
                    val delay = rs.getLong(COLUMN_DELAY)
                    val externalApiUrl = rs.getString(COLUMN_EXTERNAL_API_URL)
                    val updatedInMillis = rs.getLong(COLUMN_UPDATED_AT_IN_MILLIS)
                    val method = rs.getString(COLUMN_METHOD)

                    val allParams = Params.instance.getAll(Params.COLUMN_ROUTE_ID, id)

                    routeList.add(Route(id, projectId, routeName, requestBodyType, jsonReqBody, response, description, externalApiUrl, method, allParams, isSecure, delay, updatedInMillis))

                } while (rs.next())
            }
            rs.close()
            ps.close()
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

        return routeList

    }

    @Throws(SQLException::class)
    override fun get(projectName: String?, routeName: String?): Route? {

        var error: String? = null
        var route: Route? = null
        val query = "SELECT r.id, r.updated_at_in_millis,r.request_body_type,r.json_req_body, r.method, r.description, r.is_secure, r.delay, r.default_response, external_api_url FROM routes r INNER JOIN projects p ON p.id = r.project_id WHERE p.name = ? AND r.name = ? AND p.is_active = 1 AND r.is_active = 1 GROUP BY r.id LIMIT 1;"
        val con = Connection.getConnection()
        try {
            val ps = con.prepareStatement(query)
            ps.setString(1, projectName)
            ps.setString(2, routeName)
            val rs = ps.executeQuery()
            if (rs.first()) {

                val id = rs.getString(BaseTable.COLUMN_ID)
                val response = rs.getString(COLUMN_DEFAULT_RESPONSE)
                val description = rs.getString(COLUMN_DESCRIPTION)
                val isSecure = rs.getBoolean(COLUMN_IS_SECURE)
                val jsonReqBody = rs.getString(COLUMN_JSON_REQ_BODY)
                val requestBodyType = rs.getString(COLUMN_REQUEST_BODY_TYPE)
                val delay = rs.getLong(COLUMN_DELAY)
                val externalApiUrl = rs.getString(COLUMN_EXTERNAL_API_URL)
                val updatedInMillis = rs.getLong(COLUMN_UPDATED_AT_IN_MILLIS)
                val method = rs.getString(COLUMN_METHOD)

                val allParams = Params.instance.getAll(Params.COLUMN_ROUTE_ID, id)

                route = Route(id, null, routeName!!, requestBodyType, jsonReqBody, response, description, externalApiUrl, method, allParams, isSecure, delay, updatedInMillis)
            }

            rs.close()
            ps.close()

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

        if (route == null) {
            throw SQLException("No response found for $projectName:$routeName")
        }
        return route
    }

    @Throws(SQLException::class)
    fun getRouteBy(column: String, value: String): Route {

        var error: String? = null
        var route: Route? = null
        val query = String.format("SELECT r.id,r.name,r.request_body_type, r.json_req_body, r.updated_at_in_millis,r.method, r.description, r.is_secure, r.delay, r.default_response, external_api_url FROM routes r INNER JOIN projects p ON p.id = r.project_id WHERE r.%s = ? AND p.is_active = 1 AND r.is_active = 1 GROUP BY r.id LIMIT 1;", column)
        val con = Connection.getConnection()
        try {
            val ps = con.prepareStatement(query)
            ps.setString(1, value)
            val rs = ps.executeQuery()
            if (rs.first()) {

                val id = rs.getString(BaseTable.COLUMN_ID)
                val routeName = rs.getString(BaseTable.COLUMN_NAME)
                val response = rs.getString(COLUMN_DEFAULT_RESPONSE)
                val description = rs.getString(COLUMN_DESCRIPTION)
                val isSecure = rs.getBoolean(COLUMN_IS_SECURE)
                val requestBodyType = rs.getString(COLUMN_REQUEST_BODY_TYPE)
                val jsonReqBody = rs.getString(COLUMN_JSON_REQ_BODY)
                val delay = rs.getLong(COLUMN_DELAY)
                val externalApiUrl = rs.getString(COLUMN_EXTERNAL_API_URL)
                val updatedInMillis = rs.getLong(COLUMN_UPDATED_AT_IN_MILLIS)
                val method = rs.getString(COLUMN_METHOD)

                val allParams = Params.instance.getAll(Params.COLUMN_ROUTE_ID, id)

                route = Route(id, null, routeName, requestBodyType, jsonReqBody, response, description, externalApiUrl, method, allParams, isSecure, delay, updatedInMillis)
            }

            rs.close()
            ps.close()

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

        if (route == null) {
            throw SQLException("No response found for " + route!!)
        }

        return route
    }

    override fun update(route: Route): Boolean {
        var isUpdated = false
        val query = "UPDATE routes SET default_response = ?, description = ? , is_secure = ? , delay = ?, external_api_url = ?, updated_at_in_millis = ?, method = ?,request_body_type=?, json_req_body = ?  WHERE name = ? AND project_id = ?;"
        val con = Connection.getConnection()
        try {
            val ps = con.prepareStatement(query)

            val r = if (route.defaultResponse == null) get(BaseTable.COLUMN_ID, route.id!!, COLUMN_DEFAULT_RESPONSE, true) else route.defaultResponse

            //set
            ps.setString(1, r)
            ps.setString(2, route.description)
            ps.setBoolean(3, route.isSecure)
            ps.setLong(4, route.delay)
            ps.setString(5, route.externalApiUrl)
            ps.setLong(6, System.currentTimeMillis())
            ps.setString(7, route.method)
            ps.setString(8, route.requestBodyType)
            ps.setString(9, route.jsonReqBody)
            // where
            ps.setString(10, route.name)
            ps.setString(11, route.projectId)

            Params.instance.updateParamFromRoute(route)

            isUpdated = ps.executeUpdate() == 1
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
        if (!isUpdated) {
            throw IllegalArgumentException("Failed to update json")
        }
        return true
    }

    @Throws(SQLException::class)
    fun updateBaseOGAPIURL(projectId: String, oldBaseUrl: String, newBaseUrl: String) {
        val query = String.format("UPDATE routes SET %s = REPLACE(%s, ?, ?) WHERE INSTR(%s, ?) > 0 AND project_id = ?;", COLUMN_EXTERNAL_API_URL, COLUMN_EXTERNAL_API_URL, COLUMN_EXTERNAL_API_URL)
        val con = Connection.getConnection()
        var error: String? = null
        try {
            val ps = con.prepareStatement(query)
            ps.setString(1, oldBaseUrl)
            ps.setString(2, newBaseUrl)
            ps.setString(3, oldBaseUrl)
            ps.setString(4, projectId)
            ps.executeUpdate()
            ps.close()

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
    }

    companion object {
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_REQUEST_BODY_TYPE = "request_body_type"
        const val COLUMN_JSON_REQ_BODY = "json_req_body"
        const val COLUMN_DEFAULT_RESPONSE = "default_response"
        const val COLUMN_PROJECT_ID = "project_id"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_IS_SECURE = "is_secure"
        const val COLUMN_DELAY = "delay"
        const val COLUMN_EXTERNAL_API_URL = "external_api_url"
        const val COLUMN_UPDATED_AT_IN_MILLIS = "updated_at_in_millis"
        const val KEY_PARAMS = "params"
        const val COLUMN_METHOD = "method"
        val instance = Routes()
    }
}
