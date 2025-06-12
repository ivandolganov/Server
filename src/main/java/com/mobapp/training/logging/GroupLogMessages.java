package com.mobapp.training.logging;

public final class GroupLogMessages {

    public static final String GET_ALL_GROUPS = "Получен список всех групп";
    public static final String CREATE_GROUP = "Создание группы";
    public static final String CREATE_GROUP_FINAL = "Группа с название {} и кодом {} создана";
    public static final String GET_GROUPS_NOT_IN_COURSE = "Получены группы, не привязанные к курсу: {}";
    public static final String GET_GROUPS_BY_TEACHER = "Получены группы преподавателя: {}";
    public static final String GET_STUDENTS_IN_GROUP = "Получен список студентов в группе {} по курсу {}";
    public static final String GET_STUDENT_STATS = "Получена статистика студента {} по курсу {} и группе {}";
    public static final String GET_GROUP_STATS = "Получена статистика группы {} по курсу {}";
    public static final String FOUND_ALL_GROUPS = "Найдено {} групп";
    public static final String GROUP_DELETE_START = "Начато удаление группы ID: {}";
    public static final String COURSES_UNLINKED = "Удалено связей с курсами: {}";
    public static final String STUDENTS_UNLINKED = "Удалено студентов из группы: {}";
    public static final String GROUP_DELETED = "Группа удалена ID: {}";
    public static final String GROUP_EDIT_START = "Редактирование группы";
    public static final String COURSE_NOT_FOUND = "Курс с ID {} не найден";
    public static final String FOUND_GROUPS_NOT_IN_COURSE = "Найдено {} групп, не входящих в курс";
    public static final String STUDENTS_ADDED = "Добавлено студентов в группу: {}";
    public static final String PROCESSING_COURSE = "Обработка курса: {} ({})";
    public static final String FOUND_TEACHER_GROUPS = "Найдено {} групп преподавателя";
    public static final String COURSE_NOT_FOUND_FOR_GROUP = "Курс не найден для группы: {}";
    public static final String GROUP_NOT_FOUND = "Группа с ID {} не найдена";

    public static final String GET_USER_GROUPS_NOT_IN_ANY_COURSE =
            "Getting groups for user {} that are not in any course";
    public static final String FOUND_USER_GROUPS_NOT_IN_ANY_COURSE =
            "Found {} groups for user {} not in any course";

    public static final String STUDENT_NOT_FOUND = "Студент с ID {} не найден";
    public static final String MODULES_FOUND = "Найдено {} модулей";
    public static final String COURSE_AVERAGE_CALCULATED = "Расчет завершен. Средний балл по курсу: {}";

    public static final String GROUP_STATS_CALCULATED = "Расчет статистики завершен для группы {} по курсу {}";
    private GroupLogMessages() {
    }
}
