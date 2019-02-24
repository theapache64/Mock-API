package com.theah64.mock_api.models

class Model(
        val name: String,
        val properties: List<Property>
) {
    class Property(
            val dataType: String,
            val variableName: String
    )
}
