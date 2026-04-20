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
    public List<Sensor> getSensors(@QueryParam("type") String type) {
        if (type != null && !type.isEmpty()) {
            return inventory.getSensorsByType(type);
        }
        return inventory.getAllSensors();
    }

    @POST
    public Response createSensor(Sensor sensor) {
        // Validate Room exists
        if (inventory.getRoom(sensor.getRoomId()) == null) {
            throw new LinkedResourceNotFoundException("Room " + sensor.getRoomId() + " does not exist.");
        }
        
        inventory.addSensor(sensor);
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    @GET
    @Path("/{sensorId}")
    public Response getSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = inventory.getSensor(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(sensor).build();
    }

    /**
     * Sub-resource locator for readings.
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
