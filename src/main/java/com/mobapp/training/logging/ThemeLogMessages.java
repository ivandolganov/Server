package com.mobapp.training.logging;

public final class ThemeLogMessages {

    // Controller logs
    public static final String CREATE_THEME_REQUEST = "Запрос создания темы. Название: {}, Количество слов: {}";

    public static final String EDIT_THEME_REQUEST = "Запрос редактирования темы. ID темы: {}";

    public static final String DELETE_THEME_REQUEST = "Запрос удаления темы. ID темы: {}";

    // Service logs
    public static final String THEME_CREATED = "Тема создана. ID: {}, Название: {}";
    public static final String WORDS_ADDED = "Добавлены слова к теме. ID темы: {}, Количество: {}";

    public static final String THEME_EDIT_START = "Редактирование темы. ID темы: {}";
    public static final String THEME_NOT_FOUND = "Тема не найдена. ID темы: {}";
    public static final String THEME_UPDATED = "Тема обновлена. ID темы: {}";
    public static final String NEW_WORDS_ADDED = "Добавлены новые слова. ID темы: {}, Количество: {}";

    public static final String THEME_DELETION_START = "Удаление темы. ID темы: {}";
    public static final String WORDS_REMOVED = "Слова удалены. ID темы: {}";
    public static final String THEME_DELETED = "Тема удалена. ID темы: {}";
}