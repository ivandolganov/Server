package com.mobapp.training.logging;

public final class LoginLogMessages {
    public static final String ATTEMPT_LOGIN = "Попытка входа с email: {}";
    public static final String LOGIN_SUCCESS = "Вход успешен для email: {}";
    public static final String LOGIN_FAILURE = "Неудачная попытка входа для email: {}";
    public static final String FINDING_USER = "Поиск пользователя с email: {}";
    public static final String USER_NOT_FOUND = "Пользователь с email {} не найден";
    public static final String USER_FOUND = "Пользователь найден: {}";
    public static final String INVALID_PASSWORD = "Неверный пароль для пользователя с email: {}";
    public static final String PASSWORD_CONFIRMED = "Пароль подтверждён, выполняется аутентификация";
    public static final String AUTH_SUCCESS = "Аутентификация прошла успешно для пользователя: {}";
    public static final String JWT_CREATED = "JWT токен создан для пользователя: {}";
}
