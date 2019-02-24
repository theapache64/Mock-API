package com.theah64.mock_api

import com.theah64.mock_api.utils.MailHelper
import com.theah64.mock_api.utils.SecretConstants
import com.theah64.webengine.utils.WebEngineConfig

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener

/**
 * Created by theapache64 on 1/11/17.
 */
@WebListener
class WebEngineListener : ServletContextListener {

    override fun contextInitialized(servletContextEvent: ServletContextEvent) {
        WebEngineConfig.init("jdbc/mock_api", "jdbc/mock_api", false, "http://localhost:8080/mock_api",
                "http://theapache64.com/mock_api")

        MailHelper.init(SecretConstants.GMAIL_USERNAME, SecretConstants.GMAIL_PASSWORD)
    }

    override fun contextDestroyed(servletContextEvent: ServletContextEvent) {

    }
}
