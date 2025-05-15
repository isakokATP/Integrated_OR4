package com.int221.int221backend.exception;

public class DuplicateBrandNameException extends RuntimeException {
    public DuplicateBrandNameException(String name) {
        super("Brand name '" + name + "' already exists.");
    }
}
