package com.theah64.mock_api.database


import com.theah64.mock_api.models.Response
import com.theah64.webengine.database.BaseTable
import com.theah64.webengine.database.querybuilders.AddQueryBuilder
import com.theah64.webengine.database.querybuilders.QueryBuilderException
import com.theah64.webengine.database.querybuilders.SelectQueryBuilder

import java.sql.SQLException

/**
 * Created by theapache64 on 1/12/17.
 */
class Responses : com.theah64.webengine.database.BaseTable<Response>("responses") {


    @Throws(QueryBuilderException::class, SQLException::class)
    override fun getAll(whereColumn: String, whereColumnValue: String): List<Response> {
        return SelectQueryBuilder.Builder(tableName) { rs ->
            Response(
                    rs.getString(BaseTable.COLUMN_ID),
                    rs.getString(BaseTable.COLUMN_NAME),
                    rs.getString(COLUMN_ROUTE_ID),
                    rs.getString(COLUMN_RESPONSE)
            )
        }.where(whereColumn, whereColumnValue)
                .select(arrayOf(BaseTable.COLUMN_ID, BaseTable.COLUMN_NAME, COLUMN_ROUTE_ID, COLUMN_RESPONSE))
                .orderBy(BaseTable.COLUMN_ID + " DESC")
                .build()
                .all
    }

    @Throws(QueryBuilderException::class, SQLException::class)
    override fun addv3(response: Response): String {
        return AddQueryBuilder.Builder(tableName)
                .add(BaseTable.COLUMN_NAME, response.name)
                .add(COLUMN_ROUTE_ID, response.routeId)
                .add(COLUMN_RESPONSE, response.response)
                .doneAndReturn().toString()
    }

    companion object {
        val COLUMN_ID = "id"
        val COLUMN_NAME = "name"
        val COLUMN_RESPONSE = "response"
        val COLUMN_ROUTE_ID = "route_id"
        val instance = Responses()
    }
}
