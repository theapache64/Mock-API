package com.theah64.mock_api.models

/**
 * Created by theapache64 on 30/11/17.
 */
class Param(val id: String?,
            val name: String,
            val routeId: String?,
            val dataType: String,
            val defaultValue: String?,
            val description: String?,
            val isRequired: Boolean) {

    override fun equals(other: Any?): Boolean {
        var result = false
        if (other == null || other.javaClass != javaClass) {
            result = false
        } else {
            val param = other as Param?
            if (this.name == param!!.name) {
                result = true
            }
        }
        return result
    }


    override fun toString(): String {
        return name
    }

    fun toStringAll(): String {
        return "Param{" +
                "id='" + id + '\''.toString() +
                ", name='" + name + '\''.toString() +
                ", routeId='" + routeId + '\''.toString() +
                ", dataType='" + dataType + '\''.toString() +
                ", defaultValue='" + defaultValue + '\''.toString() +
                ", description='" + description + '\''.toString() +
                ", isRequired=" + isRequired +
                '}'.toString()
    }

    companion object {
        val DATA_TYPE_FILE = "File"
    }
}
