package org.westminster.smartcampus.resource;

import org.westminster.smartcampus.exception.SensorUnavailableException;
import org.westminster.smartcampus.model.Sensor;
import org.westminster.smartcampus.model.SensorReading;
import org.westminster.smartcampus.service.InventoryService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Sub-resource for managing readings for a specific sensor.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;
    private final InventoryService inventory = InventoryService.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public List<SensorReading> getHistory() {
        if (inventory.getSensor(sensorId) == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return inventory.getReadingsForSensor(sensorId);
    }

    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = inventory.getSensor(sensorId);
        if (sensor == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        // Business Logic: Cannot accept readings if in MAINTENANCE
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException("Sensor " + sensorId + " is in MAINTENANCE and cannot accept readings.");
        }

        inventory.addReading(sensorId, reading);
        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
