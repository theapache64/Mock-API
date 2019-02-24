package com.theah64.mock_api.models

/**
 * Created by theapache64 on 11/1/18.
 */
class DiffView(
        val title: String,
        val oldDataId: String,
        val newDataId: String,
        val diffOutputId: String,
        val newData: String,
        val oldData: String
)
