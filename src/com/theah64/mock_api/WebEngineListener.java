package com.theah64.mock_api;

import com.theah64.mock_api.utils.SecretConstants;
import com.theah64.webengine.utils.MailHelper;
import com.theah64.webengine.utils.WebEngineConfig;
import com.tinify.Tinify;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by theapache64 on 1/11/17.
 */
@WebListener
public class WebEngineListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("WebEngine configured");
        WebEngineConfig.init("jdbc/mock_api", "jdbc/mock_api", false, "http://localhost:8080/mock_api",
                "http://theapache64.com/mock_api");

        MailHelper.init(SecretConstants.GMAIL_USERNAME, SecretConstants.GMAIL_PASSWORD);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
