package org.westminster.smartcampus.service;

import org.westminster.smartcampus.model.Room;
import org.westminster.smartcampus.model.Sensor;
import org.westminster.smartcampus.model.SensorReading;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Singleton service for managing in-memory storage of campus entity data.
 */
public class InventoryService {
    private static InventoryService instance;

    // Thread-safe storage
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    private InventoryService() {
        // Initialize with sample data for demonstration
        initializeSampleData();
    }

    public static synchronized InventoryService getInstance() {
        if (instance == null) {
            instance = new InventoryService();
        }
        return instance;
    }

    // --- Room Operations ---

    public void addRoom(Room room) {
        rooms.put(room.getId().toUpperCase(), room);
    }

    public Room getRoom(String id) {
        return id == null ? null : rooms.get(id.toUpperCase());
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public boolean deleteRoom(String id) {
        if (id == null) return false;
        String normalizedId = id.toUpperCase();
        Room room = rooms.get(normalizedId);
        if (room != null && room.getSensorIds().isEmpty()) {
            rooms.remove(normalizedId);
            return true;
        }
        return false; // Cannot delete if room has sensors or doesn't exist
    }

    // --- Sensor Operations ---

    public void addSensor(Sensor sensor) {
        sensors.put(sensor.getId().toUpperCase(), sensor);
        // Link to room idempotently
        Room room = rooms.get(sensor.getRoomId().toUpperCase());
        if (room != null) {
            synchronized (room.getSensorIds()) {
                if (!room.getSensorIds().contains(sensor.getId().toUpperCase())) {
                    room.getSensorIds().add(sensor.getId().toUpperCase());
                }
            }
        }
    }

    public Sensor getSensor(String id) {
        return sensors.get(id);
    }

    public List<Sensor> getAllSensors() {
        return new ArrayList<>(sensors.values());
    }

    public List<Sensor> getSensorsByType(String type) {
        return sensors.values().stream()
                .filter(s -> s.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    // --- Reading Operations ---

    public void addReading(String sensorId, SensorReading reading) {
        readings.computeIfAbsent(sensorId, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(reading);
        
        // Side Effect: Update parent sensor's currentValue
        // Guaranteed update via synchronized block to prevent race conditions during concurrent readings
        Sensor sensor = sensors.get(sensorId);
        if (sensor != null) {
            synchronized (sensor) {
                sensor.setCurrentValue(reading.getValue());
            }
        }
    }

    public List<SensorReading> getReadingsForSensor(String sensorId) {
        return readings.getOrDefault(sensorId, Collections.emptyList());
    }

    // --- Helpers ---

    private void initializeSampleData() {
        // Sample Room
        Room lib = new Room("LIB-301", "Library Quiet Study", 50);
        addRoom(lib);

        // Sample Sensor
        Sensor temp = new Sensor("TEMP-001", "Temperature", "ACTIVE", "LIB-301");
        addSensor(temp);

        // Sample Reading
        addReading("TEMP-001", new SensorReading(22.5));
    }
}
