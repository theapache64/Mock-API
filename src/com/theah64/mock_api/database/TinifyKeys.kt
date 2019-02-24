package com.theah64.mock_api.database

import com.theah64.mock_api.models.TinifyKey
import com.theah64.webengine.database.BaseTable
import com.theah64.webengine.database.querybuilders.AddQueryBuilder
import com.theah64.webengine.database.querybuilders.QueryBuilderException
import com.theah64.webengine.database.querybuilders.SelectQueryBuilder

import java.sql.SQLException

class TinifyKeys private constructor() : BaseTable<TinifyKey>("tinify_keys") {

    val leastUsedKey: TinifyKey?
        @Throws(QueryBuilderException::class, SQLException::class)
        get() = SelectQueryBuilder(tableName, { rs ->
            TinifyKey(
                    rs.getString(BaseTable.Companion.COLUMN_ID),
                    rs.getString(COLUMN_KEY),
                    rs.getString(COLUMN_EMAIL),
                    rs.getString(COLUMN_USAGE)
            )
        }, arrayOf(BaseTable.Companion.COLUMN_ID, COLUMN_KEY, COLUMN_EMAIL, COLUMN_USAGE),
                arrayOf(BaseTable.Companion.COLUMN_IS_ACTIVE),
                arrayOf(BaseTable.Companion.TRUE),
                "1",
                COLUMN_USAGE
        ).get()

    override val all: List<TinifyKey>
        @Throws(QueryBuilderException::class, SQLException::class)
        get() = SelectQueryBuilder.Builder(tableName) { rs ->
            TinifyKey(
                    rs.getString(BaseTable.Companion.COLUMN_ID),
                    rs.getString(COLUMN_KEY),
                    rs.getString(COLUMN_EMAIL),
                    rs.getString(COLUMN_USAGE)
            )
        }.select(arrayOf(BaseTable.Companion.COLUMN_ID, COLUMN_KEY, COLUMN_EMAIL, COLUMN_USAGE))
                .orderBy(COLUMN_USAGE)
                .build()
                .all

    @Throws(SQLException::class, QueryBuilderException::class)
    override fun add(tKey: TinifyKey): Boolean {
        return AddQueryBuilder.Builder(tableName)
                .add(COLUMN_KEY, tKey.key)
                .add(COLUMN_EMAIL, tKey.email)
                .add(COLUMN_USAGE, tKey.usage)
                .done()
    }

    companion object {

        val COLUMN_KEY = "_key"
        val COLUMN_ID = "id"
        val COLUMN_IS_ACTIVE = "is_active"
        val COLUMN_EMAIL = "email"
        val COLUMN_USAGE = "_usage"

        val FALSE = "0"
        val TRUE = "1"
        val INSTANCE = TinifyKeys()
    }
}
