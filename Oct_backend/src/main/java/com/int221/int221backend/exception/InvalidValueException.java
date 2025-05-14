package com.int221.int221backend.exception;

public class InvalidValueException extends RuntimeException {
    public InvalidValueException(String message) {
        super(message);
    }
}