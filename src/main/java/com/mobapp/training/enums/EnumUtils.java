package com.mobapp.training.enums;

import com.mobapp.training.exception.InvalidEnumValueException;

public class EnumUtils {

    public static <T extends Enum<T>> T safeValueOf(Class<T> enumClass, String value, String fieldName) {
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new InvalidEnumValueException(fieldName, value);
        }
    }
}
