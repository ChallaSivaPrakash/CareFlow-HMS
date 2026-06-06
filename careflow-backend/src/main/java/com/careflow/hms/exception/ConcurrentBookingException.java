package com.careflow.hms.exception;

public class ConcurrentBookingException extends RuntimeException {
    public ConcurrentBookingException(String message, Throwable cause) {
        super(message, cause);
    }
}
