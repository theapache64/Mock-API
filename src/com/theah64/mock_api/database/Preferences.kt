package com.theah64.mock_api.database

import com.theah64.mock_api.models.Preference
import com.theah64.webengine.database.BaseTable
import com.theah64.webengine.database.querybuilders.QueryBuilderException
import com.theah64.webengine.database.querybuilders.SelectQueryBuilder
import java.sql.ResultSet

import java.sql.SQLException

/**
 * Created by theapache64 on 15/1/18.
 */
class Preferences private constructor() : BaseTable<Preference>("preferences") {

    @Throws(QueryBuilderException::class, SQLException::class)
    fun get(): Preference? {
        return SelectQueryBuilder.Builder(tableName, object : SelectQueryBuilder.Callback<Preference> {
            override fun getNode(rs: ResultSet?): Preference {
                return Preference(
                        rs!!.getString(COLUMN_DEFAULT_SUCCESS_RESPONSE),
                        rs.getString(COLUMN_DEFAULT_ERROR_RESPONSE),
                        rs.getString(COLUMN_BASE_RESPONSE_STRUCTURE),
                        rs.getString(COLUMN_SURPISE_IMAGE),
                        rs.getString(COLUMN_SURPISE_QUOTE),
                        rs.getBoolean(COLUMN_IS_ONLINE)
                )
            }
        }).select(arrayOf(COLUMN_DEFAULT_SUCCESS_RESPONSE, COLUMN_SURPISE_QUOTE, COLUMN_SURPISE_IMAGE, COLUMN_DEFAULT_ERROR_RESPONSE, COLUMN_BASE_RESPONSE_STRUCTURE, COLUMN_IS_ONLINE))
                .limit("1")
                .build()
                .get()
    }

    companion object {
        val instance = Preferences()
        private val COLUMN_DEFAULT_SUCCESS_RESPONSE = "default_success_response"
        private val COLUMN_DEFAULT_ERROR_RESPONSE = "default_error_response"
        private val COLUMN_BASE_RESPONSE_STRUCTURE = "base_response_structure"
        private val COLUMN_IS_ONLINE = "is_online"
        private val COLUMN_SURPISE_IMAGE = "surprise_image"
        private val COLUMN_SURPISE_QUOTE = "surprise_quote"
    }
}
