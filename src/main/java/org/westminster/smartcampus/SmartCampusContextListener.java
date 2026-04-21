package org.westminster.smartcampus;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class SmartCampusContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String contextPath = sce.getServletContext().getContextPath();
        
        System.out.println("\n\n");
        System.out.println("**************************************************");
        System.out.println("SMART CAMPUS API IS NOW LIVE!");
        System.out.println("**************************************************");
        System.out.println("Access the API at:");
        System.out.println("http://localhost:8080" + contextPath + "/api/v1");
        System.out.println("**************************************************\n\n");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Smart Campus API Shutting Down...");
    }
}
