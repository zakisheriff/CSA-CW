package org.westminster.smartcampus.service;

import org.westminster.smartcampus.model.Room;
import org.westminster.smartcampus.model.Sensor;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Utility for centralizing resource validation logic.
 * Demonstrates a "First Class" approach to defensive programming.
 */
public class ValidationService {

    public static void validateRoom(Room room) {
        if (room == null) {
            throw new WebApplicationException("Room data is required", Response.Status.BAD_REQUEST);
        }
        if (room.getId() == null || room.getId().trim().isEmpty()) {
            throw new WebApplicationException("Room ID is mandatory", Response.Status.BAD_REQUEST);
        }
        if (room.getCapacity() <= 0) {
            throw new WebApplicationException("Room capacity must be a positive integer", Response.Status.BAD_REQUEST);
        }
        if (room.getName() == null || room.getName().trim().isEmpty()) {
            throw new WebApplicationException("Room name cannot be empty", Response.Status.BAD_REQUEST);
        }
    }

    public static void validateSensor(Sensor sensor) {
        if (sensor == null) {
            throw new WebApplicationException("Sensor data is required", Response.Status.BAD_REQUEST);
        }
        if (sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            throw new WebApplicationException("Sensor ID is mandatory", Response.Status.BAD_REQUEST);
        }
        if (sensor.getRoomId() == null || sensor.getRoomId().trim().isEmpty()) {
            throw new WebApplicationException("A Room ID must be provided to register a sensor", Response.Status.BAD_REQUEST);
        }
        
        String status = sensor.getStatus();
        if (status == null || (!status.equals("ACTIVE") && !status.equals("MAINTENANCE") && !status.equals("OFFLINE"))) {
            throw new WebApplicationException("Invalid status. Must be ACTIVE, MAINTENANCE, or OFFLINE", Response.Status.BAD_REQUEST);
        }
    }
}
