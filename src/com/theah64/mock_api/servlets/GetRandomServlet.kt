package com.theah64.mock_api.servlets

import com.theah64.mock_api.utils.APIResponse
import com.theah64.webengine.utils.PathInfo
import com.theah64.webengine.utils.Request
import com.thedeanda.lorem.Lorem
import com.thedeanda.lorem.LoremIpsum
import org.json.JSONException

import javax.servlet.annotation.WebServlet
import java.io.IOException
import java.sql.SQLException

@WebServlet(urlPatterns = ["/v1/get_random"])
class GetRandomServlet : AdvancedBaseServlet() {

    override val isSecureServlet: Boolean
        get() = false

    override val requiredParameters: Array<String>?
        @Throws(Request.RequestException::class)
        get() = arrayOf(KEY_RANDOM_WHAT, KEY_COUNT)

    @Throws(Request.RequestException::class, IOException::class, JSONException::class, SQLException::class, PathInfo.PathInfoException::class)
    override fun doAdvancedPost() {
        val randomWhat = getStringParameter(KEY_RANDOM_WHAT)
        val count = getIntParameter(KEY_COUNT, 1)
        val output: String

        val lorem = LoremIpsum.getInstance()

        when (randomWhat) {
            TYPE_WORDS -> output = capitalize(lorem.getWords(count, count))
            TYPE_PARAGRAPHS -> output = lorem.getParagraphs(count, count)
            TYPE_NAME -> output = lorem.name
            TYPE_PHONE -> output = lorem.phone
            TYPE_CITY -> output = lorem.city
            TYPE_STATE -> output = lorem.stateFull
            TYPE_COUNTRY -> output = lorem.country
            TYPE_MALE_NAME -> output = lorem.nameMale
            TYPE_FEMALE_NAME -> output = lorem.nameFemale
            TYPE_FIRST_NAME -> output = lorem.firstName
            TYPE_LAST_NAME -> output = lorem.lastName
            else -> throw Request.RequestException("Invalid randomWhat: $randomWhat")
        }

        writer!!.write(APIResponse("Done", "random_output", output).response)
    }

    private fun capitalize(line: String): String {
        return Character.toUpperCase(line[0]) + line.substring(1)
    }

    companion object {

        private val KEY_RANDOM_WHAT = "random_what"
        private val KEY_COUNT = "count"

        private val TYPE_WORDS = "words"
        private val TYPE_PARAGRAPHS = "paragraphs"
        private val TYPE_NAME = "name"
        private val TYPE_PHONE = "phone"
        private val TYPE_CITY = "city"
        private val TYPE_STATE = "state"
        private val TYPE_COUNTRY = "country"
        private val TYPE_MALE_NAME = "male_name"
        private val TYPE_FEMALE_NAME = "female_name"
        private val TYPE_FIRST_NAME = "first_name"
        private val TYPE_LAST_NAME = "last_name"
    }
}
