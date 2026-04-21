package org.westminster.smartcampus;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Standard JAX-RS Application configuration.
 * Extends the standard javax.ws.rs.core.Application as required by the coursework spec.
 * This ensures the code is server-neutral and follows standard JEE patterns.
 */
@ApplicationPath("/api/v1")
public class AppConfig extends Application {
    // Note: Package scanning is configured in web.xml to maintain 
    // compatibility with Tomcat/GlassFish deployment patterns.
    public AppConfig() {
        System.out.println("--------------------------------------------------");
        System.out.println("Smart Campus API Configuration Loaded via JAX-RS");
        System.out.println("--------------------------------------------------");
    }
}
