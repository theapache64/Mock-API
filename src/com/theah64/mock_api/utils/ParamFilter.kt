package com.theah64.mock_api.utils

import org.json.JSONObject

object ParamFilter {

    fun filterAllParams(jsonString: String): List<String> {
        val jo = JSONObject(jsonString)
        @Suppress("UNCHECKED_CAST")
        return (jo.keys() as Iterator<String>).asSequence().toList()
    }

    fun filterRequiredParams(jsonString: String): List<String> {

        // looping through each key
        val params = mutableListOf<String>()

        if(jsonString.isNotEmpty()){
            val jo = JSONObject(jsonString)

            jo.keys().forEach { key ->
                val value = jo.get(key as String)
                if (
                // ends with ? = optional
                        (value is String && !value.endsWith("?")) ||
                        // 0 = optional
                        ((value is Number) && value != 0) ||
                        // false = optional
                        ((value is Boolean) && value == false)
                ) {
                    // required
                    params.add(key)
                }
            }
        }


        return params
    }
}