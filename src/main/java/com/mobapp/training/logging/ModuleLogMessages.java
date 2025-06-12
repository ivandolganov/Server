package com.mobapp.training.logging;

public final class ModuleLogMessages {
    // Service logs
    public static final String GET_MODULES_START = "Начало получения модулей курса. Course ID: {}, User ID: {}";
    public static final String GET_MODULES_SUCCESS = "Модули курса успешно получены. Course ID: {}, User ID: {}";
    public static final String USER_HAS_NO_GROUPS = "У пользователя нет групп. User ID: {}";
    public static final String COURSE_ACCESS_DENIED = "Доступ к курсу запрещен. Course ID: {}, User ID: {}";
    public static final String MODULE_NOT_FOUND = "Модуль не найден. Module ID: {}";

    public static final String CREATE_MODULE_START = "Создание модуля. Course ID: {}, User ID: {}";
    public static final String CREATE_MODULE_SUCCESS = "Модуль успешно создан. Module ID: {}, Course ID: {}, User ID: {}";
    public static final String CREATE_EXERCISES_START = "Создание упражнений для модуля. Module ID: {}, User ID: {}";

    public static final String DELETE_MODULE_START = "Начало удаления модуля. Module ID: {}, User ID: {}";
    public static final String DELETE_MODULE_SUCCESS = "Модуль успешно удален. Module ID: {}, User ID: {}";
    public static final String DELETE_EXERCISES_START = "Удаление упражнений модуля. Module ID: {}, Count: {}";
    public static final String DELETE_EXERCISES_SUCCESS = "Упражнения успешно удалены. Module ID: {}, Count: {}";

    public static final String UPDATE_MODULE_START = "Начало обновления модуля. Module ID: {}, User ID: {}";
    public static final String UPDATE_MODULE_SUCCESS = "Модуль успешно обновлен. Module ID: {}, User ID: {}";
    public static final String UPDATE_EXERCISES_START = "Обновление упражнений модуля. Module ID: {}, User ID: {}";
    public static final String UPDATE_EXERCISES_SUCCESS = "Упражнения успешно обновлены. Module ID: {}, User ID: {}";

    public static final String NUMBER_PARAMS_DELETED = "Удалено параметров числовых упражнений: {}";
    public static final String DATE_PARAMS_DELETED = "Удалено параметров упражнений с датами: {}";
    public static final String NAMES_FACES_PARAMS_DELETED = "Удалено параметров упражнений с именами и лицами: {}";
    public static final String WORD_SET_PARAMS_DELETED = "Удалено параметров упражнений с наборами слов: {}";
}