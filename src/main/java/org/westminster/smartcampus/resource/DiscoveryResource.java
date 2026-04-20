package org.westminster.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Root Discovery endpoint for the Smart Campus API.
 */
@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscovery() {
        Map<String, Object> discovery = new HashMap<>();
        discovery.put("version", "1.0.0");
        discovery.put("description", "Smart Campus Sensor & Room Management API");
        discovery.put("admin_contact", "lead-backend@westminster.ac.uk");
        
        // HATEOAS: Resource links
        Map<String, String> links = new HashMap<>();
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        discovery.put("resources", links);

        return Response.ok(discovery).build();
    }
}
