package com.theah64.mock_api.utils

import com.theah64.mock_api.models.Route

object RouteUtils {
    fun order(routeList: List<Route>): List<Route> {
        return routeList.apply {
            for (route in this) {

            }
        }
    }

    fun getOrderMap(): Map<String, Int> {
        return mapOf(
                "get_public_preference" to 1,
                "signup" to 2,
                "login" to 3,
                "forgot_password" to 4,
                "fcm" to 5,
                "search" to 6

        )
    }
}