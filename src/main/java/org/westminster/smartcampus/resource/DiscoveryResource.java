package org.westminster.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Root Discovery endpoint for the Smart Campus API.
 * Demonstrates HATEOAS by providing dynamic links to primary resource collections.
 */
@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscovery(@Context UriInfo uriInfo) {
        Map<String, Object> discovery = new LinkedHashMap<>();
        discovery.put("api_name", "Smart Campus API");
        discovery.put("version", "1.1.0");
        discovery.put("status", "OPERATIONAL");
        discovery.put("admin_contact", "lead-backend@westminster.ac.uk");
        
        // HATEOAS: Dynamic Resource links using UriInfo
        Map<String, String> links = new LinkedHashMap<>();
        links.put("self", uriInfo.getAbsolutePath().toString());
        links.put("rooms", uriInfo.getBaseUriBuilder().path(SensorRoom.class).build().toString());
        links.put("sensors", uriInfo.getBaseUriBuilder().path(SensorResource.class).build().toString());
        
        discovery.put("_links", links);

        return Response.ok(discovery).build();
    }
}
