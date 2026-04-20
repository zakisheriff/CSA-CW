package org.westminster.smartcampus.exception;

/**
 * Thrown when attempting to record readings from a sensor in maintenance.
 */
public class SensorUnavailableException extends RuntimeException {
    public SensorUnavailableException(String message) {
        super(message);
    }
}
