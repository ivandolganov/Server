package com.mobapp.training.logging;

public final class CourseLogMessages {
    // Основные операции
    public static final String GET_USER_COURSES = "Получение курсов пользователя с ID: {} (Роль: {})";
    public static final String CREATE_COURSE_START = "Создание нового курса. Пользователь: {}";
    public static final String GET_EDIT_PAGE = "Запрос страницы редактирования курса. ID курса: {}";
    public static final String SAVE_COURSE_CHANGES = "Сохранение изменений курса. ID курса: {}";
    public static final String DELETE_COURSE_START = "Удаление курса. ID курса: {}";
    
    // Ошибки
    public static final String USER_NOT_FOUND = "Пользователь не найден. ID: {}";
    public static final String ACCESS_DENIED = "Доступ запрещен для роли: {}";
    public static final String GET_STUDENT_COURSES_START = "Получение курсов для студента ID: {}";
    public static final String STUDENT_HAS_NO_GROUPS = "У студента нет групп. ID студента: {}";
    public static final String CALCULATE_AVG_RESULT = "Расчет среднего результата для курса {}: {}%";

    // Для getCoursesForTeacher
    public static final String GET_TEACHER_COURSES_START = "Получение курсов преподавателя ID: {}";
    public static final String TEACHER_HAS_NO_COURSES = "У преподавателя нет курсов. ID преподавателя: {}";

    // Для createCourse
    public static final String CREATE_COURSE_VALIDATION = "Проверка групп для нового курса. Запрошено: {}, найдено: {}";
    public static final String COURSE_CREATED = "Создан новый курс. ID: {}, название: {}, групп: {}";

    // Для editCourse
    public static final String EDIT_COURSE_START = "Запрос данных для редактирования курса ID: {}";
    public static final String EDIT_COURSE_MODULES = "Найдено модулей для курса {}: {}";
    public static final String EDIT_COURSE_GROUPS = "Найдено групп для курса {}: {}";

    // Для saveCourseChanges
    public static final String SAVE_COURSE_START = "Сохранение изменений курса ID: {}";
    public static final String COURSE_NAME_UPDATED = "Обновлено название курса {}: '{}' -> '{}'";
    public static final String COURSE_GROUPS_ADDED = "Добавлены группы к курсу {}: {}";
    public static final String COURSE_GROUPS_REMOVED = "Удалены группы из курса {}: {}";

    // Для deleteCourse
    public static final String DELETE_COURSE_VALIDATION = "Проверка прав на удаление курса {} пользователем {}";
    public static final String DELETE_COURSE_MODULES = "Удаление модулей курса {}. Найдено модулей: {}";
    public static final String DELETE_COURSE_EXERCISES = "Удаление упражнений модуля {}. Удалено упражнений: {}";
    public static final String COURSE_DELETED = "Курс {} полностью удален";
}