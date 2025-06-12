package com.mobapp.training.logging;

public final class SettingsLogMessages {

    // Controller logs
    public static final String ADD_GROUP_REQUEST = "Запрос на добавление группы. Student ID: {}, Group Code: {}";

    public static final String PASSWORD_CHANGE_REQUEST = "Запрос смены пароля. User ID: {}";

    public static final String GET_SETTINGS_REQUEST = "Запрос настроек. User ID: {}";

    public static final String SAVE_SETTINGS_REQUEST = "Запрос сохранения настроек. User ID: {}";

    // Service logs
    public static final String ADD_GROUP_START = "Добавление группы. Student ID: {}, Group Code: {}";
    public static final String STUDENT_NOT_FOUND = "Студент не найден. Student ID: {}";
    public static final String GROUP_NOT_FOUND = "Группа не найдена. Group Code: {}";
    public static final String GROUP_ADDED = "Группа добавлена студенту. Student ID: {}, Group Code: {}";

    public static final String PASSWORD_CHANGE_START = "Смена пароля. User ID: {}";
    public static final String PASSWORD_CHANGED = "Пароль изменен. User ID: {}";

    public static final String GET_SETTINGS_START = "Получение настроек. User ID: {}";
    public static final String SETTINGS_RETRIEVED = "Настройки получены. User ID: {}";

    public static final String SAVE_SETTINGS_START = "Сохранение настроек. User ID: {}";
    public static final String SETTINGS_SAVED = "Настройки сохранены. User ID: {}";
    public static final String GENDER_UPDATED = "Пол обновлен. User ID: {}, Новый пол: {}";
}