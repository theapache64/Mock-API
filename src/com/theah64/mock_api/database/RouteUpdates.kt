package com.theah64.mock_api.database

import com.sun.istack.internal.Nullable
import com.theah64.mock_api.models.RouteUpdate
import com.theah64.webengine.database.BaseTable
import com.theah64.webengine.database.querybuilders.AddQueryBuilder
import com.theah64.webengine.database.querybuilders.QueryBuilderException
import com.theah64.webengine.database.querybuilders.SelectQueryBuilder

import java.sql.SQLException

/**
 * Created by theapache64 on 10/1/18.
 */
class RouteUpdates private constructor() : BaseTable<RouteUpdate>("route_updates") {

    //private final String id, key, routeId, method, params, delay, description, defaultResponse;

    @Throws(SQLException::class, QueryBuilderException::class)
    override fun add(newInstance: RouteUpdate): Boolean {
        return AddQueryBuilder.Builder(tableName)
                .add(COLUMN_KEY, newInstance.key)
                .add(COLUMN_ROUTE_ID, newInstance.routeId)
                .add(COLUMN_METHOD, newInstance.method)
                .add(COLUMN_PARAMS, emptyIfNull(newInstance.params))
                .add(COLUMN_DELAY, emptyIfNull(newInstance.delay))
                .add(COLUMN_DESCRIPTION, emptyIfNull(newInstance.description))
                .add(COLUMN_DEFAULT_RESPONSE, emptyIfNull(newInstance.defaultResponse))
                .done()
    }

    private fun emptyIfNull(data: String?): String {
        return data ?: ""
    }

    @Throws(QueryBuilderException::class, SQLException::class)
    override fun get(column: String, value: String): RouteUpdate? {
        return SelectQueryBuilder.Builder(tableName, SelectQueryBuilder.Callback { rs ->
            RouteUpdate(
                    rs.getString(BaseTable.Companion.COLUMN_ID),
                    rs.getString(COLUMN_KEY),
                    rs.getString(COLUMN_ROUTE_ID),
                    rs.getString(COLUMN_METHOD),
                    rs.getString(COLUMN_PARAMS),
                    rs.getString(COLUMN_DELAY),
                    rs.getString(COLUMN_DESCRIPTION),
                    rs.getString(COLUMN_DEFAULT_RESPONSE),
                    rs.getLong(COLUMN_CREATED_AT))
        }).select(arrayOf(BaseTable.Companion.COLUMN_ID, COLUMN_KEY, COLUMN_ROUTE_ID, COLUMN_METHOD, COLUMN_PARAMS, COLUMN_DELAY, COLUMN_DESCRIPTION, COLUMN_DEFAULT_RESPONSE, String.format("UNIX_TIMESTAMP(%s) AS %s", COLUMN_CREATED_AT, COLUMN_CREATED_AT)))
                .where(column, value)
                .limit(1)
                .build()
                .get()
    }


    @Throws(QueryBuilderException::class, SQLException::class)
    override fun getAll(whereColumn: String, whereColumnValue: String): List<RouteUpdate> {
        return SelectQueryBuilder.Builder(tableName, SelectQueryBuilder.Callback { rs ->
            RouteUpdate(
                    rs.getString(BaseTable.Companion.COLUMN_ID),
                    rs.getString(COLUMN_KEY),
                    rs.getString(COLUMN_ROUTE_ID),
                    rs.getString(COLUMN_METHOD),
                    rs.getString(COLUMN_PARAMS),
                    rs.getString(COLUMN_DELAY),
                    rs.getString(COLUMN_DESCRIPTION),
                    rs.getString(COLUMN_DEFAULT_RESPONSE),
                    rs.getLong(COLUMN_CREATED_AT))
        }).select(arrayOf(BaseTable.Companion.COLUMN_ID, COLUMN_KEY, COLUMN_ROUTE_ID, COLUMN_METHOD, COLUMN_PARAMS, COLUMN_DELAY, COLUMN_DESCRIPTION, COLUMN_DEFAULT_RESPONSE, String.format("UNIX_TIMESTAMP(%s) AS %s", COLUMN_CREATED_AT, COLUMN_CREATED_AT)))
                .where(whereColumn, whereColumnValue)
                .build()
                .all

    }

    @Throws(QueryBuilderException::class, SQLException::class)
    fun getSecondLast(limitUpdateId: String, column: String, value: String): RouteUpdate? {
        return getWithLimit("1,1", limitUpdateId, column, value)
    }

    @Throws(QueryBuilderException::class, SQLException::class)
    fun getLast(column: String, value: String): RouteUpdate? {
        return getWithLimit("1", null, column, value)
    }

    @Throws(QueryBuilderException::class, SQLException::class)
    private fun getWithLimit(limit: String, @Nullable limitUpdateId: String?, column: String, value: String): RouteUpdate? {

        val whereKeys: Array<String>
        val whereValues: Array<String>

        if (limitUpdateId != null) {
            whereKeys = arrayOf(column, "id<")
            whereValues = arrayOf(value, limitUpdateId)
        } else {
            whereKeys = arrayOf(column)
            whereValues = arrayOf(value)
        }


        return SelectQueryBuilder.Builder(tableName, SelectQueryBuilder.Callback { rs ->
            RouteUpdate(
                    rs.getString(BaseTable.Companion.COLUMN_ID),
                    rs.getString(COLUMN_KEY),
                    rs.getString(COLUMN_ROUTE_ID),
                    rs.getString(COLUMN_METHOD),
                    rs.getString(COLUMN_PARAMS),
                    rs.getString(COLUMN_DELAY),
                    rs.getString(COLUMN_DESCRIPTION),
                    rs.getString(COLUMN_DEFAULT_RESPONSE),
                    rs.getLong(COLUMN_CREATED_AT))
        }).select(arrayOf(BaseTable.Companion.COLUMN_ID, COLUMN_KEY, COLUMN_ROUTE_ID, COLUMN_METHOD, COLUMN_PARAMS, COLUMN_DELAY, COLUMN_DESCRIPTION, COLUMN_DEFAULT_RESPONSE, String.format("UNIX_TIMESTAMP(%s) AS %s", COLUMN_CREATED_AT, COLUMN_CREATED_AT)))
                .where(whereKeys, whereValues)
                .orderBy("id DESC")
                .limit(limit)
                .build()
                .get()
    }

    companion object {

        val COLUMN_KEY = "_key"
        val COLUMN_ROUTE_ID = "route_id"
        val instance = RouteUpdates()
        private val COLUMN_METHOD = "method"
        private val COLUMN_PARAMS = "params"
        private val COLUMN_DELAY = "delay"
        private val COLUMN_DESCRIPTION = "description"
        private val COLUMN_DEFAULT_RESPONSE = "default_response"
        private val COLUMN_CREATED_AT = "created_at"
    }
}
