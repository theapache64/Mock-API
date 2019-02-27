package com.theah64.mock_api.lab2

import com.theah64.webengine.utils.ParamFilter

const val JSON_STRING = "{ \"id\": true , \"profile_image_url\": \"https://picsum.photos/500/500/?image=600\", \"title\": \"Doug McGee started following you\", \"created_at_timestamp\": \"2018-10-14 06:03:52\" }"

fun main() {
    val params: List<String> = ParamFilter.filterRequiredParams(JSON_STRING)
    println("Params are ${params.toList()}")
}

