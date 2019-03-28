package com.theah64.mock_api.utils

import com.theah64.webengine.utils.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileNotFoundException
import java.net.URL
import java.util.regex.Pattern

class GoogleSheetUtils(
        private val sheetUrl: String
) {

    val jsonArray: JSONArray

    init {
        require(isValidSheetUrl(sheetUrl)) { "Invalid GoogleSheetURL" }
        val csvData = getDataFrom(sheetUrl)
        this.jsonArray = toJsonArray(csvData)
    }


    companion object {

        const val RESP_JSON_SHEET_FORMAT_REGEX = "!SHEET\\[(https://docs\\.google\\.com/spreadsheets.+&output=csv)]"
        const val SINGLE_SHEET_URL_REGEX = "https://docs\\.google\\.com/spreadsheets.+&output=csv"

        private fun isValidSheetUrl(sheetUrl: String): Boolean {
            return sheetUrl.matches(Regex(SINGLE_SHEET_URL_REGEX))
        }

        private fun getDataFrom(sheetUrl: String): String {
            try {
                return URL(sheetUrl).readText()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                throw Request.RequestException("Invalid sheet URL : $sheetUrl")
            }
        }

        private fun toJsonArray(csvData: String): JSONArray {
            // data
            val jsonArray = JSONArray()

            // Splitting column names and data
            val rows = csvData.split("\n")

            // Collecting column names
            val colNames = rows[0].split(",")
            for (i in 1 until rows.size) {
                val row = rows[i]
                val vals = row.split(",")
                val jsonObject = JSONObject()
                for (value in vals.withIndex()) {
                    jsonObject.put(toUnderscore(colNames[value.index].trim()), value.value.trim())
                }
                jsonArray.put(jsonObject)
            }

            return jsonArray
        }

        fun toUnderscore(string: String): String {
            return string.toLowerCase().trim().replace(Regex("\\s+"), "_")
        }


        fun generate(_jsonResp: String): String {
            var jsonResp = _jsonResp
            val pattern = Pattern.compile(RESP_JSON_SHEET_FORMAT_REGEX)
            val matcher = pattern.matcher(jsonResp)
            while (matcher.find()) {
                val fullMatch = matcher.group()
                val url = matcher.group(1)
                val jsonArr = GoogleSheetUtils(url).jsonArray
                jsonResp = jsonResp.replace("\"$fullMatch\"", jsonArr.toString())
            }
            return jsonResp
        }
    }

}
