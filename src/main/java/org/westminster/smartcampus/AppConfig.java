package org.westminster.smartcampus;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api/v1")
public class AppConfig extends ResourceConfig {
    public AppConfig() {
        // Register resources and features
        packages("org.westminster.smartcampus.resource", 
                 "org.westminster.smartcampus.exception",
                 "org.westminster.smartcampus.filter");
        
        // Register Jackson for JSON support
        register(JacksonFeature.class);
        
        System.out.println("Smart Campus API Configuration Initialized at /api/v1");
    }
}
