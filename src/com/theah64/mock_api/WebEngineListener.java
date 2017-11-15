package com.theah64.mock_api;

import com.theah64.mock_api.utils.Inflector;

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
        System.out.println("OK");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
