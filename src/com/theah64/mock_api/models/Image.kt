package com.theah64.mock_api.models

data class Image(
        var id: String?,
        val projectId: String,
        val tinifyKeyId: String,
        var imageUrl: String,
        val thumbUrl: String,
        val filePath: String,
        var isCompressed: Boolean
)

