package com.theah64.mock_api

import com.theah64.mock_api.utils.RouteUtils

fun main() {
    val list = listOf(
            "login",
            "get_public_preference",
            "get_book",
            "search_book",
            "signup"
    )

    val ordered = RouteUtils.getByOrder(list)
    println(ordered)
}