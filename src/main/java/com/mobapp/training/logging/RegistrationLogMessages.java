package com.mobapp.training.logging;

public final class RegistrationLogMessages {

    // Controller logs
    public static final String CHECK_EMAIL_REQUEST = "Запрос проверки email. Email: {}";

    public static final String CHECK_GROUP_REQUEST = "Запрос проверки группы. Код группы: {}";

    public static final String REGISTER_REQUEST = "Запрос регистрации. Email: {}, Имя: {}";

    // Service logs
    public static final String EMAIL_CHECK_START = "Проверка email. Email: {}";
    public static final String EMAIL_EXISTS = "Email уже существует. Email: {}";
    public static final String EMAIL_DOMAIN_CHECK = "Проверка домена email. Домен: {}";
    public static final String TEACHER_DOMAIN_FOUND = "Найден домен преподавателя. Домен: {}";

    public static final String GROUP_CHECK_START = "Проверка группы. Код группы: {}";

    public static final String REGISTRATION_START = "Начало регистрации. Email: {}";
    public static final String USER_CREATED = "Пользователь создан. ID: {}, Email: {}";
    public static final String TOKEN_GENERATED = "Токен сгенерирован для пользователя. ID: {}";
}