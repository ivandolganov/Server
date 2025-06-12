package com.mobapp.training.logging;

public final class ExerciseResultLogMessages {

    // Controller logs
    public static final String COMPLETE_EXERCISE_REQUEST = "Запрос на сохранение результата упражнения. Exercise ID: {}, User ID: {}";
    // Service logs
    public static final String SAVE_RESULT_START = "Сохранение результата упражнения. Exercise ID: {}, User ID: {}";
    public static final String EXERCISE_NOT_FOUND = "Упражнение не найдено. Exercise ID: {}";
    public static final String USER_NOT_FOUND = "Пользователь не найден. User ID: {}";
    public static final String RESULT_SAVED = "Результат сохранен. Result ID: {}, Exercise ID: {}, User ID: {}, Правильных ответов: {}/{}";
    public static final String SAVE_RESULT_SUCCESS = "Результат успешно сохранен. Exercise ID: {}, User ID: {}";
}