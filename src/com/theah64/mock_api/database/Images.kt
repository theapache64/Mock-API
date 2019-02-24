package com.theah64.mock_api.database

import com.theah64.mock_api.models.Image
import com.theah64.webengine.database.BaseTable
import com.theah64.webengine.database.querybuilders.AddQueryBuilder
import com.theah64.webengine.database.querybuilders.QueryBuilderException
import com.theah64.webengine.database.querybuilders.SelectQueryBuilder
import com.theah64.webengine.database.querybuilders.UpdateQueryBuilder

import java.sql.ResultSet
import java.sql.SQLException

class Images private constructor() : BaseTable<Image>("images") {

    //id, projectId, tinifyKeyId, imageUrl, thumbUrl;
    @Throws(SQLException::class, QueryBuilderException::class)
    override fun add(newInstance: Image): Boolean {
        return addv3(newInstance) != "-1"
    }

    @Throws(QueryBuilderException::class, SQLException::class)
    override fun addv3(newInstance: Image): String {
        return AddQueryBuilder.Builder(tableName)
                .add(COLUMN_PROJECT_ID, newInstance.projectId)
                .add(COLUMN_TINIFY_KEY_ID, newInstance.tinifyKeyId)
                .add(COLUMN_IMAGE_URL, newInstance.imageUrl)
                .add(COLUMN_THUMB_URL, newInstance.thumbUrl)
                .add(COLUMN_FILE_PATH, newInstance.filePath)
                .add(COLUMN_IS_COMPRESSED, newInstance.isCompressed)
                .doneAndReturn().toString()
    }

    @Throws(QueryBuilderException::class, SQLException::class)
    override fun getAll(whereColumn: String, whereColumnValue: String): List<Image> {
        return SelectQueryBuilder<Image>(
                tableName,
                SelectQueryBuilder.Callback<Image> { this.getImageFromResultSet(it) },
                arrayOf(BaseTable.Companion.COLUMN_ID, COLUMN_PROJECT_ID, COLUMN_TINIFY_KEY_ID, COLUMN_IMAGE_URL, COLUMN_THUMB_URL, COLUMN_FILE_PATH, COLUMN_IS_COMPRESSED),
                arrayOf(whereColumn), arrayOf(whereColumnValue), SelectQueryBuilder.UNLIMITED, BaseTable.Companion.COLUMN_ID + " DESC"
        ).all
    }

    @Throws(QueryBuilderException::class, SQLException::class)
    override fun get(column1: String, value1: String, column2: String, value2: String): Image {
        return SelectQueryBuilder<Image>(
                tableName,
                SelectQueryBuilder.Callback<Image> { this.getImageFromResultSet(it) },
                arrayOf(BaseTable.Companion.COLUMN_ID, COLUMN_PROJECT_ID, COLUMN_TINIFY_KEY_ID, COLUMN_IMAGE_URL, COLUMN_THUMB_URL, COLUMN_FILE_PATH, COLUMN_IS_COMPRESSED),
                arrayOf(column1, column2), arrayOf(value1, value2), "1", BaseTable.Companion.COLUMN_ID + " DESC"

        ).get()
    }

    @Throws(SQLException::class)
    private fun getImageFromResultSet(rs: ResultSet): Image {
        return Image(
                rs.getString(BaseTable.Companion.COLUMN_ID),
                rs.getString(COLUMN_PROJECT_ID),
                rs.getString(COLUMN_TINIFY_KEY_ID),
                rs.getString(COLUMN_IMAGE_URL),
                rs.getString(COLUMN_THUMB_URL),
                rs.getString(COLUMN_FILE_PATH),
                rs.getBoolean(COLUMN_IS_COMPRESSED))
    }

    @Throws(SQLException::class, QueryBuilderException::class)
    override fun update(t: Image): Boolean {
        return UpdateQueryBuilder.Builder(tableName)
                .set(COLUMN_IMAGE_URL, t.imageUrl)
                .set(COLUMN_IS_COMPRESSED, t.isCompressed)
                .where(BaseTable.Companion.COLUMN_ID, t.id)
                .build()
                .done()
    }

    companion object {

        val COLUMN_ID = "id"
        val COLUMN_PROJECT_ID = "project_id"
        val COLUMN_IMAGE_URL = "image_url"
        val COLUMN_THUMB_URL = "thumb_url"
        val COLUMN_FILE_PATH = "file_path"
        val COLUMN_IS_COMPRESSED = "is_compressed"
        val INSTANCE = Images()
        private val COLUMN_TINIFY_KEY_ID = "tinify_key_id"
    }
}
