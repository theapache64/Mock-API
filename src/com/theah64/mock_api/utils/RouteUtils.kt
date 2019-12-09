package com.theah64.mock_api.utils

import com.theah64.mock_api.models.Route

object RouteUtils {
    private val orderMap = mapOf(
            "public_preference" to 1,
            "signup" to 2,
            "login" to 3,
            "forgot_password" to 4,
            "fcm" to 5,
            "search" to 6,
            "load" to 7,
            "get" to 8,
            "add" to 9,
            "remove" to 10
    )

    fun getByOrder(routeList: List<String>): List<String> {
        return routeList.sortedBy { route ->
            for (mapItem in orderMap) {
                if (route.contains(mapItem.key)) {
                    return@sortedBy mapItem.value
                }
            }
            orderMap.size + 1
        }
    }


    fun order(routeList: List<Route>): List<Route> {
        return routeList.sortedBy { route ->
            for (mapItem in orderMap) {
                if (route.name.contains(mapItem.key)) {
                    return@sortedBy mapItem.value
                }
            }
            orderMap.size + 1
        }
    }
}