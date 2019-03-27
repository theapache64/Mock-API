package com.theah64.mock_api.utils

import com.theah64.webengine.utils.Request
import java.lang.StringBuilder
import java.util.regex.Pattern

class MarkDownUtils {
    companion object {
        const val TABLE_HEAD_REGEX = "<thead>.+(<tr>.+<\\/tr>).+<\\/thead>"
        const val TABLE_BODY_REGEX = "<tbody>.+(<tr>.+<\\/tr>).+<\\/tbody>"

        fun toMarkDownTable(_tableData: String): String {

            var tableData = _tableData
            tableData = tableData.replace("\n", "")
            tableData = tableData.replace(Regex("\\s+"), " ")

            // getting headers
            val headers = getHeadings(tableData)

            // Getting rows
            val rows = getRows(tableData)

            if (headers != null) {

                val sBuilder = StringBuilder("|")
                val lineBuilder = StringBuilder("|")

                // Adding headers
                for (header in headers) {
                    sBuilder.append(header).append("|")
                    lineBuilder.append(get('-', header.length - 1)).append("|")
                }

                sBuilder.append("\n").append(lineBuilder).append("\n")

                rows?.let { tableRows ->
                    for (row in tableRows) {

                        sBuilder.append("|")
                        for (value in row) {
                            sBuilder.append(value).append("|")
                        }
                        sBuilder.append("\n")
                    }
                }


                return sBuilder.toString()
            } else {
                throw Request.RequestException("Invalid table data, couldn't get table header data")
            }
        }

        private fun get(char: Char, length: Int): Any {
            val sb = StringBuilder()
            for (i in 0..length) {
                sb.append(char)
            }
            return sb.toString()
        }

        fun getRows(tableData: String): List<List<String>>? {

            var headings = mutableListOf<List<String>>()
            val pattern = Pattern.compile(TABLE_BODY_REGEX)
            val matcher = pattern.matcher(tableData)
            if (matcher.find()) {
                var tBody = matcher.group(0)
                tBody = tBody.replace(Regex("(<\\s*tbody>|</\\s*tbody>)"), "")
                tBody.split("<tr>")
                        .filter { row -> row.trim().isNotEmpty() }
                        .map { row ->
                            val tds = row.split("<td>")
                                    .filter { td -> td.trim().isNotEmpty() }
                                    .map { td -> td.replace(Regex("(</td>|</tr>)"), "").trim() }


                            headings.add(tds)
                        }
            }
            return headings
        }

        private fun getHeadings(tableData: String): List<String>? {
            var headings: List<String>? = null
            val pattern = Pattern.compile(TABLE_HEAD_REGEX)
            val matcher = pattern.matcher(tableData)
            if (matcher.find()) {
                var trHead = matcher.group(1)
                trHead = trHead.replace(Regex("(<\\s*tr\\s*>|<\\s*th\\s*>|</\\s*tr\\s*>)"), "")
                headings = trHead.split("</th>")
                        .map { value -> value.trim() }
                        .filter { value -> value.isNotEmpty() }
            }
            return headings
        }
    }
}