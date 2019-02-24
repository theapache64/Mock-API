package com.theah64.mock_api.models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by theapache64 on 10/1/18.
 */
class RouteUpdate(
        val id: String?,
        val key: String,
        val routeId: String,
        val method: String,
        val params: String,
        val delay: String,
        val description: String,
        val defaultResponse: String,
        createdAt: Long
) {
    val createdAt: String?

    init {
        this.createdAt = if (createdAt != -1L) READABLE_DATE_FORMAT.format(Date(createdAt * 1000)) else null
    }

    override fun toString(): String {
        return "RouteUpdate{" +
                "id='" + id + '\''.toString() +
                ", key='" + key + '\''.toString() +
                ", routeId='" + routeId + '\''.toString() +
                ", method='" + method + '\''.toString() +
                ", params='" + params + '\''.toString() +
                ", delay='" + delay + '\''.toString() +
                ", description='" + description + '\''.toString() +
                ", defaultResponse='" + defaultResponse + '\''.toString() +
                '}'.toString()
    }

    companion object {

        private val READABLE_DATE_FORMAT = SimpleDateFormat("MMM d, EEE, ''yy (HH:mm:ss)", Locale.getDefault())
    }
}
