package org.westminster.smartcampus.exception;

/**
 * Thrown when attempting to delete a room that still has sensors assigned.
 */
public class RoomNotEmptyException extends RuntimeException {
    public RoomNotEmptyException(String message) {
        super(message);
    }
}
