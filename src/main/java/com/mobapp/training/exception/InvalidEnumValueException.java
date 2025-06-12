package com.mobapp.training.exception;

public class InvalidEnumValueException extends RuntimeException {
    public InvalidEnumValueException(String field, String value) {
        super("Недопустимое значение для поля " + field + ": " + value);
    }
}