package com.mobapp.training.logging;

public final class ProfileLogMessages {

    // Controller logs
    public static final String GET_PROFILE_REQUEST = "Запрос на получение профиля. User ID: {}";

    // Service logs
    public static final String PROFILE_SERVICE_START = "Начало обработки запроса профиля. User ID: {}";
    public static final String USER_NOT_FOUND = "Пользователь не найден. User ID: {}";
    public static final String PROFILE_SERVICE_SUCCESS = "Профиль успешно обработан. User ID: {}";
}