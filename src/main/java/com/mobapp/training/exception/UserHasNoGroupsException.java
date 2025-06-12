package com.mobapp.training.exception;

public class UserHasNoGroupsException extends RuntimeException {
    public UserHasNoGroupsException(String message) {
        super(message);
    }
}