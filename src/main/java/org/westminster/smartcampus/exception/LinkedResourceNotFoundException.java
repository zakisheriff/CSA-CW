package org.westminster.smartcampus.exception;

/**
 * Thrown when a linked resource (e.g. roomId in a sensor) is not found.
 */
public class LinkedResourceNotFoundException extends RuntimeException {
    public LinkedResourceNotFoundException(String message) {
        super(message);
    }
}
