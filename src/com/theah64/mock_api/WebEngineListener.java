package com.theah64.mock_api;

import com.theah64.webengine.utils.WebEngineConfig;

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
                "http://theapache64.cf:8080/mock_api");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
