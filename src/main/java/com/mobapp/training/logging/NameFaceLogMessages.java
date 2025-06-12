package com.mobapp.training.logging;

public final class NameFaceLogMessages {

    // Controller logs
    public static final String GET_RANDOM_NAMES_REQUEST = "Запрос на получение случайных имен. Exercise ID: {}";

    public static final String GENERATE_NAMES_REQUEST = "Запрос на генерацию имен пользователем. Format: {}, Count: {}";

    // Service logs
    public static final String GET_NAMES_START = "Начало получения имен. Exercise ID: {}";
    public static final String EXERCISE_NOT_FOUND = "Упражнение не найдено. Exercise ID: {}";
    public static final String GET_NAMES_SUCCESS = "Имена успешно получены. Exercise ID: {}, Count: {}, Format: {}";
    
    public static final String GENERATE_NAMES_USER_START = "Начало генерации имен пользователем. Format: {}, Count: {}";
    public static final String GENERATE_NAMES_USER_SUCCESS = "Имена успешно сгенерированы. Format: {}, Count: {}";
}