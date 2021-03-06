package com.theah64.mock_api.lab2

import com.theah64.webengine.utils.Request
import java.lang.StringBuilder
import java.util.regex.Pattern


const val TABLE_DATA = "<table class=\"table table-bordered \">\n" +
        "    <thead>\n" +
        "    <tr>\n" +
        "        <th>Returned Key</th>\n" +
        "        <th>Description</th>\n" +
        "        <th>Example</th>\n" +
        "    </tr>\n" +
        "    </thead>\n" +
        "    <tbody>\n" +
        "    <tr>\n" +
        "        <td>error</td>\n" +
        "        <td>The returned status for the API call, can be either 'true' or 'false'</td>\n" +
        "        <td>true</td>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "        <td>message</td>\n" +
        "        <td>Either the error message or the successful message</td>\n" +
        "        <td>OK</td>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "        <td>data</td>\n" +
        "        <td>If 'error' is returned as 'false' the API query results will be inside 'data'</td>\n" +
        "        <td>data</td>\n" +
        "    </tr>\n" +
        "    </tbody>\n" +
        "</table>"

fun main() {
    val markDown = MarkDownUtils.toMarkDownTable(TABLE_DATA)
    println(markDown)
}

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




