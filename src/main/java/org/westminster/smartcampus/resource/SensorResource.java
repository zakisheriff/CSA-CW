package org.westminster.smartcampus.resource;

import org.westminster.smartcampus.exception.LinkedResourceNotFoundException;
import org.westminster.smartcampus.model.Sensor;
import org.westminster.smartcampus.service.InventoryService;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

/**
 * Resource for managing /sensors path.
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final InventoryService inventory = InventoryService.getInstance();

    @GET
    public List<Sensor> getSensors(@QueryParam("type") String type, @Context UriInfo uriInfo) {
        List<Sensor> sensors = (type != null && !type.isEmpty()) 
                ? inventory.getSensorsByType(type) 
                : inventory.getAllSensors();
        
        for (Sensor sensor : sensors) {
            // Clear existing links to prevent side-effect accumulation in singleton state
            sensor.getLinks().clear();
            String sensorUri = uriInfo.getBaseUriBuilder()
                    .path(SensorResource.class)
                    .path(sensor.getId())
                    .build().toString();
            sensor.getLinks().put("self", sensorUri);
            sensor.getLinks().put("readings", sensorUri + "/readings");
        }
        return sensors;
    }

    @POST
    public Response createSensor(Sensor sensor) {
        // Professional validation layer for POJO integrity
        org.westminster.smartcampus.service.ValidationService.validateSensor(sensor);

        // Dependency Validation (422 check)
        if (inventory.getRoom(sensor.getRoomId()) == null) {
            throw new LinkedResourceNotFoundException("Room " + sensor.getRoomId() + " does not exist.");
        }
        
        inventory.addSensor(sensor);
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    @GET
    @Path("/{sensorId}")
    public Response getSensor(@PathParam("sensorId") String sensorId, @Context UriInfo uriInfo) {
        Sensor sensor = inventory.getSensor(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // HATEOAS: Populate dynamic links
        sensor.getLinks().clear();
        sensor.getLinks().put("self", uriInfo.getAbsolutePath().toString());
        sensor.getLinks().put("readings", uriInfo.getAbsolutePathBuilder().path("readings").build().toString());

        return Response.ok(sensor).build();
    }

    /**
     * Sub-resource locator for readings.
     * Effectively delegates responsibility for the /readings sub-path to SensorReadingResource.
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        // Validate Sensor existence before delegating to preserve resource integrity
        if (inventory.getSensor(sensorId) == null) {
            throw new WebApplicationException("Sensor not found", Response.Status.NOT_FOUND);
        }
        return new SensorReadingResource(sensorId);
    }
}
