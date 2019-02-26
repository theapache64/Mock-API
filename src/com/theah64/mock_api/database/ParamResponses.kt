package com.theah64.mock_api.database

import com.theah64.mock_api.models.ParamResponse
import com.theah64.webengine.database.BaseTable
import com.theah64.webengine.database.querybuilders.AddQueryBuilder
import com.theah64.webengine.database.querybuilders.QueryBuilderException
import com.theah64.webengine.database.querybuilders.SelectQueryBuilder

import java.sql.SQLException

class ParamResponses private constructor() : BaseTable<ParamResponse>("param_responses") {

    @Throws(QueryBuilderException::class, SQLException::class)
    override fun getAll(whereColumn: String, whereColumnValue: String): List<ParamResponse> {
        return SelectQueryBuilder(tableName, SelectQueryBuilder.Callback { rs ->
            ParamResponse(
                    rs.getString("id"),
                    rs.getString("route_id"),
                    rs.getString("param_id"),
                    rs.getString("param_value"),
                    rs.getString("response_id"),
                    rs.getString("rel_opt")
            )
        }, arrayOf(BaseTable.COLUMN_ID, COLUMN_ROUTE_ID, COLUMN_PARAM_ID, COLUMN_PARAM_VALUE, COLUMN_RESPONSE_ID, COLUMN_REL_OPT), arrayOf(whereColumn), arrayOf(whereColumnValue), SelectQueryBuilder.UNLIMITED, BaseTable.Companion.COLUMN_ID + " DESC").all
    }

    /**
     * route_id,param_id,param_value, response_id, rel_opt
     *
     * @return
     * @throws SQLException
     * @throws QueryBuilderException
     */
    @Throws(SQLException::class, QueryBuilderException::class)
    override fun add(newInstance: ParamResponse): Boolean {
        return AddQueryBuilder.Builder(tableName)
                .add(COLUMN_ROUTE_ID, newInstance.routeId)
                .add(COLUMN_PARAM_ID, newInstance.paramId)
                .add(COLUMN_PARAM_VALUE, newInstance.paramValue)
                .add(COLUMN_RESPONSE_ID, newInstance.responseId)
                .add(COLUMN_REL_OPT, newInstance.relOpt)
                .done()
    }

    companion object {

        const val COLUMN_ROUTE_ID = "route_id"
        val instance = ParamResponses()
        const val COLUMN_PARAM_ID = "param_id"
        const val COLUMN_PARAM_VALUE = "param_value"
        const val COLUMN_RESPONSE_ID = "response_id"
        const val COLUMN_REL_OPT = "rel_opt"
    }
}
