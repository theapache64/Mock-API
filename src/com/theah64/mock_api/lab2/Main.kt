package com.theah64.mock_api.lab2

import com.theah64.mock_api.utils.GoogleSheetUtils


const val SHEET_CSV_URL = "https://docs.google.com/spreadsheets/d/e/2PACX-1vTtuGrZzkXV34FuHrGGtYmKG0kTOPS0dMhvY5RrKIAldyRQAhM9VMSjOMwJjh-aHe-V6gptv4bZkIZT/pub?gid=0&single=true&output=csv"
const val JSON_DATA = "{\n" +
        "  \"error\": false,\n" +
        "  \"message\": \"This is a sample success message\",\n" +
        "  \"data\": {\n" +
        "    \"assassins\": \"!SHEET[https://docs.google.com/spreadsheets/d/e/2PACX-1vTtuGrZzkXV34FuHrGGtYmKG0kTOPS0dMhvY5RrKIAldyRQAhM9VMSjOMwJjh-aHe-V6gptv4bZkIZT/pub?gid=0&single=true&output=csv]\",\n" +
        "    \"another_list\": \"!SHEET[https://docs.google.com/spreadsheets/d/e/2PACX-1vTtuGrZzkXV34FuHrGGtYmKG0kTOPS0dMhvY5RrKIAldyRQAhM9VMSjOMwJjh-aHe-V6gptv4bZkIZT/pub?gid=1157683088&single=true&output=csv]\"\n" +
        "  }\n" +
        "}"

fun main() {
    val genData = GoogleSheetUtils.generate(JSON_DATA)
    println(genData)
}


