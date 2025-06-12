package com.mobapp.training.logging;

public final class UserWordExercisesLogMessages {

    // Controller logs
    public static final String CREATE_THEME_REQUEST = "Запрос создания темы. User ID: {}, Title: {}, Words count: {}";
    public static final String ADD_WORDS_REQUEST = "Запрос добавления слов. Theme ID: {}, Words count: {}";
    public static final String SAVE_RESULT_REQUEST = "Запрос сохранения результата. User ID: {}, Theme ID: {}, Time: {}";
    public static final String GET_THEMES_REQUEST = "Запрос получения тем. User ID: {}";
    public static final String EDIT_THEME_REQUEST = "Запрос редактирования темы. User ID: {}, Theme ID: {}";
    public static final String DELETE_THEME_REQUEST = "Запрос удаления темы. Theme ID: {}";
    public static final String GET_THEME_WORDS_REQUEST = "Запрос получения слов темы. User ID: {}, Theme ID: {}";

    // Service logs
    public static final String THEME_CREATION_START = "Создание темы. User ID: {}, Title: {}";
    public static final String THEME_CREATED = "Тема создана. Theme ID: {}, Words count: {}";
    public static final String USER_NOT_FOUND = "Пользователь не найден. User ID: {}";
    public static final String ADD_WORDS_START = "Добавление слов. Theme ID: {}, Words count: {}";
    public static final String WORDS_ADDED = "Слова добавлены. Theme ID: {}";
    public static final String THEME_NOT_FOUND = "Тема не найдена. Theme ID: {}";
    public static final String RESULT_SAVED = "Результат сохранен. User ID: {}, Theme ID: {}";
    public static final String THEMES_RETRIEVED = "Темы получены. User ID: {}, Count: {}";
    public static final String THEME_DELETION_START = "Удаление темы. Theme ID: {}";
    public static final String THEME_DELETED = "Тема удалена. Theme ID: {}";
    public static final String THEME_EDIT_START = "Редактирование темы. Theme ID: {}";
    public static final String THEME_UPDATED = "Тема обновлена. Theme ID: {}, New words count: {}";
}