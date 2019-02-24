package com.theah64.mock_api.utils


import com.theah64.mock_api.database.Projects

/**
 * Created by shifar on 31/12/15.
 */
class HeaderSecurity @Throws(AuthorizationException::class)
constructor(private val authorization: String?) {
    lateinit var projectId: String
        private set

    val failureReason: String
        get() = if (this.authorization == null) REASON_API_KEY_MISSING else REASON_INVALID_API_KEY

    init {
        isAuthorized()
    }//Collecting header from passed request

    /**
     * Used to identify if passed API-KEY has a valid victim.
     */
    @Throws(AuthorizationException::class)
    private fun isAuthorized() {

        if (this.authorization == null) {
            //No api key passed along with request
            throw AuthorizationException("Unauthorized access")
        }

        Projects.INSTANCE.get(Projects.COLUMN_API_KEY, this.authorization, Projects.COLUMN_ID, true).let { projectId ->

            if (projectId == null) {
                throw AuthorizationException("Invalid API KEY: " + this.authorization)
            }

            this.projectId = projectId
        }

    }

    inner class AuthorizationException(message: String) : Exception(message)

    companion object {

        val KEY_AUTHORIZATION = "Authorization"
        private val REASON_API_KEY_MISSING = "API key is missing"
        private val REASON_INVALID_API_KEY = "Invalid API key"
    }
}
