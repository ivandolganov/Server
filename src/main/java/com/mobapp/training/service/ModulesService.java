package com.mobapp.training.service;

import com.mobapp.training.controllers.CoursesController;
import com.mobapp.training.dto.request.*;
import com.mobapp.training.dto.response.*;
import com.mobapp.training.exception.AccessDeniedException;
import com.mobapp.training.exception.NotFoundException;
import com.mobapp.training.exception.UserHasNoGroupsException;
import com.mobapp.training.logging.ModuleLogMessages;
import com.mobapp.training.models.*;
import com.mobapp.training.repo.*;
import com.mobapp.training.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModulesService {
    private static final Logger logger = LoggerFactory.getLogger(CoursesController.class);

    // Repositories
    private final UsersRepository usersRepository;
    private final CourseGroupRepository courseGroupRepository;
    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseResultRepository exerciseResultRepository;
    private final NumberExerciseParamsRepository numberParamsRepository;
    private final DateExerciseParamsRepository dateParamsRepository;
    private final NamesFacesExerciseParamsRepository namesFacesParamsRepository;
    private final WordSetExerciseParamsRepository wordSetParamsRepository;
    private final ThemeRepository themeRepository;
    private final ModuleNotificationRepository moduleNotificationRepository;

    // Student-related methods
    @Transactional
    public CourseWithModulesResponse getModulesForStudent(CustomUserDetails userDetails, UUID courseId) {
        logger.info(ModuleLogMessages.GET_MODULES_START, courseId, userDetails.getId());
        Users student = getUserOrThrow(userDetails.getId());
        if(student.getRole().equals("ROLE_STUDENT")) {
            validateStudentGroups(student);
            validateCourseAccess(student, courseId);
        }

        List<Modules> modules = moduleRepository.findByCourseId(courseId);
        List<ModulesResponse> moduleResponses = modules.stream()
                .map(module -> buildModuleResponse(module, student))
                .collect(Collectors.toList());
        logger.info(ModuleLogMessages.GET_MODULES_SUCCESS, courseId, userDetails.getId());
        return buildCourseWithModulesResponse(courseId, modules, moduleResponses);
    }

    @Transactional
    public ModuleResponse getModule(UUID teacherId, UUID moduleId, UUID courseId) {
        Users student = getUserOrThrow(teacherId);
        if(student.getRole().equals("ROLE_STUDENT")) {
            validateStudentGroups(student);
            validateCourseAccess(student, courseId);
        }
        Modules module = getModuleOrThrow(moduleId);
        validateModuleCourse(module, courseId);

        Course course = getCourseByModuleOrThrow(module);

        ModuleResponse response = buildModuleResponse(module, course, student);
        return response;
    }

    // Teacher-related methods
    @Transactional
    public UUID createModule(UUID teacherId, UUID courseId, CreateModuleRequest request) {
        logger.info(ModuleLogMessages.CREATE_MODULE_START, courseId, teacherId);
        Course course = getCourseOrThrow(courseId);
        Users teacher = getUserOrThrow(teacherId);

        Modules module = createAndSaveModule(request, course);
        logger.info(ModuleLogMessages.CREATE_EXERCISES_START, module.getId(), teacherId);
        createExercisesForModule(teacher, module, request);

        ModuleNotification notification = new ModuleNotification();
        notification.setModule(module);
        moduleNotificationRepository.save(notification);

        logger.info(ModuleLogMessages.CREATE_MODULE_SUCCESS, module.getId(), courseId, teacherId);
        return module.getId();
    }

    @Transactional
    public MessageResponse deleteModule(UUID moduleId, UUID userId) {
        logger.info(ModuleLogMessages.DELETE_MODULE_START, moduleId, userId);

        Modules module = getModuleOrThrow(moduleId);
        Course course = getCourseOrThrow(module.getCourse().getId());
        validateCourseOwnership(course, userId);

        List<Exercise> exercises = exerciseRepository.findByModuleId(moduleId);
        logger.info(ModuleLogMessages.DELETE_EXERCISES_START, moduleId, exercises.size());

        deleteExerciseData(exercises);
        exerciseRepository.deleteByModule(module);
        moduleRepository.delete(module);


        logger.info(ModuleLogMessages.DELETE_EXERCISES_SUCCESS, moduleId, exercises.size());
        logger.info(ModuleLogMessages.DELETE_MODULE_SUCCESS, moduleId, userId);

        return buildDeleteSuccessMessage(module, exercises.size());
    }

    @Transactional
    public MessageResponse updateModule(UUID moduleId, Users user, UpdateModuleRequest request) {
        logger.info(ModuleLogMessages.UPDATE_MODULE_START, moduleId, user.getId());
        Modules module = getModuleOrThrow(moduleId);
        updateModuleDetails(module, request);

        List<Exercise> existingExercises = exerciseRepository.findByModuleId(moduleId);
        Map<UUID, Exercise> existingExercisesMap = existingExercises.stream()
                .collect(Collectors.toMap(Exercise::getId, e -> e));

        logger.info(ModuleLogMessages.UPDATE_EXERCISES_START, moduleId, user.getId());
        processExercises(module, user, request, existingExercisesMap);
        deleteRemovedExercises(existingExercises, request);

        logger.info(ModuleLogMessages.UPDATE_EXERCISES_SUCCESS, moduleId, user.getId());

        logger.info(ModuleLogMessages.UPDATE_MODULE_SUCCESS, moduleId, user.getId());

        return new MessageResponse("Модуль изменен!");
    }

    // Helper methods
    private Users getUserOrThrow(UUID userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private void validateStudentGroups(Users student) {
        if (student.getStudentGroups() == null || student.getStudentGroups().isEmpty()) {
            logger.warn(ModuleLogMessages.USER_HAS_NO_GROUPS, student.getId());
            throw new UserHasNoGroupsException("У пользователя нет групп");
        }
    }

    private void validateCourseAccess(Users student, UUID courseId) {
        if (!courseGroupRepository.existsByCourseIdAndGroupIn(courseId, student.getStudentGroups())) {
            logger.warn(ModuleLogMessages.COURSE_ACCESS_DENIED, courseId, student.getId());
            throw new AccessDeniedException("У студента нет доступа к курсу");
        }
    }

    private Modules getModuleOrThrow(UUID moduleId) {
        return moduleRepository.findById(moduleId)
                .orElseThrow(() -> {
                    logger.error(ModuleLogMessages.MODULE_NOT_FOUND, moduleId);
                    return new NotFoundException("Модуль не найден");
                });
    }

    private void validateModuleCourse(Modules module, UUID courseId) {
        if (!module.getCourse().getId().equals(courseId)) {
            throw new NotFoundException("Модуль не принадлежит курсу");
        }
    }

    private Course getCourseByModuleOrThrow(Modules module) {
        return courseRepository.findByModuleId(module.getId())
                .orElseThrow(() -> new NotFoundException("Курс для модуля не найден"));
    }

    private Course getCourseOrThrow(UUID courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Курс не найден"));
    }

    private void validateCourseOwnership(Course course, UUID userId) {
        if (!course.getTeacher().getId().equals(userId)) {
            throw new AccessDeniedException("Вы не являетесь владельцем курса");
        }
    }

    // Response building methods
    private ModulesResponse buildModuleResponse(Modules module, Users student) {
        ModulesResponse response = new ModulesResponse();
        response.setModuleId(module.getId());
        response.setModuleTitle(module.getName());
        response.setDescription(module.getDescription());
        response.setEndDate(module.getEndDate());
        response.setPassThreshold(module.getPassThreshold());

        List<Exercise> exercises = exerciseRepository.findByModuleId(module.getId());
        response.setAverageResult((int)calculateAverageResult(exercises, student));

        return response;
    }

    private double calculateAverageResult(List<Exercise> exercises, Users student) {
        if (exercises.isEmpty()) return 0.0;

        double totalScore = exercises.stream()
                .mapToDouble(exercise -> getExerciseResultPercent(exercise, student))
                .sum();

        return totalScore / exercises.size();
    }

    private double getExerciseResultPercent(Exercise exercise, Users student) {
        return exerciseResultRepository
                .findTopByExerciseAndStudentOrderByAttemptedAtDesc(exercise, student)
                .map(r -> (double) r.getCorrectCount() / r.getTotalCount() * 100.0)
                .orElse(0.0);
    }

    private CourseWithModulesResponse buildCourseWithModulesResponse(
            UUID courseId, List<Modules> modules, List<ModulesResponse> moduleResponses) {

        CourseWithModulesResponse response = new CourseWithModulesResponse();
        response.setCourseId(courseId);
        response.setCourseTitle(modules.isEmpty() ? "" : modules.get(0).getCourse().getName());
        response.setModules(moduleResponses);
        return response;
    }

    private ModuleResponse buildModuleResponse(Modules module, Course course, Users student) {
        ModuleResponse response = new ModuleResponse();
        response.setModuleId(module.getId());
        response.setModuleTitle(module.getName());
        response.setDescription(module.getDescription());
        response.setPassThreshold(module.getPassThreshold());
        response.setEndDate(module.getEndDate());
        response.setTeacherName(course.getTeacher().getFullName());

        List<Exercise> exercises = exerciseRepository.findByModule(module);
        processExercisesForResponse(response, exercises, student);

        return response;
    }

    private void processExercisesForResponse(ModuleResponse response, List<Exercise> exercises, Users student) {
        List<NumberExerciseParamsResponse> numberParams = new ArrayList<>();
        List<DateExerciseParamsResponse> dateParams = new ArrayList<>();
        List<NamesFacesExerciseParamsResponse> nameFaceParams = new ArrayList<>();
        List<ModuleWorldSetResponse> wordSetParams = new ArrayList<>();

        double totalPercent = 0.0;
        int countedExercises = 0;

        for (Exercise exercise : exercises) {
            double lastPercent = getExerciseResultPercent(exercise, student);
            totalPercent += lastPercent;
            countedExercises++;

            switch (exercise.getType()) {
                case NUMBER:
                    numberParamsRepository.findByExercise(exercise)
                            .ifPresent(e -> numberParams.add(buildNumberParamsResponse(e, exercise.getId(), lastPercent)));
                    break;
                case DATE:
                    dateParamsRepository.findByExercise(exercise)
                            .ifPresent(e -> dateParams.add(buildDateParamsResponse(e, exercise.getId(), lastPercent)));
                    break;
                case NAME_FACE:
                    namesFacesParamsRepository.findByExercise(exercise)
                            .ifPresent(e -> nameFaceParams.add(buildNamesFacesParamsResponse(e, exercise.getId(), lastPercent)));
                    break;
                case WORD_SET:
                    wordSetParamsRepository.findByExercise(exercise)
                            .ifPresent(e -> wordSetParams.add(buildWordSetParamsResponse(e, exercise.getId(), lastPercent)));
                    break;
            }
        }

        response.setAvgResult((int)(countedExercises > 0 ? totalPercent / countedExercises : 0.0));
        response.setNumberExerciseParams(numberParams);
        response.setDateExerciseParams(dateParams);
        response.setNamesFacesExerciseParams(nameFaceParams);
        response.setModuleWorldSetParams(wordSetParams);
    }

    // Exercise params response builders
    private NumberExerciseParamsResponse buildNumberParamsResponse(
            NumberExerciseParams params, UUID exerciseId, double lastPercent) {

        NumberExerciseParamsResponse dto = new NumberExerciseParamsResponse();
        dto.setExerciseId(exerciseId);
        dto.setBase(params.getBase());
        dto.setNumberCount(params.getNumberCount());
        dto.setDigit_lenth(params.getDigitLength());
        dto.setMinValue(params.getMinValue());
        dto.setMaxValue(params.getMaxValue());
        dto.setDisplayTimeMs(params.getDisplayTimeMs());
        dto.setLastResultPercent((int)lastPercent);
        return dto;
    }

    private DateExerciseParamsResponse buildDateParamsResponse(
            DateExerciseParams params, UUID exerciseId, double lastPercent) {

        DateExerciseParamsResponse dto = new DateExerciseParamsResponse();
        dto.setExerciseId(exerciseId);
        dto.setDateCount(params.getDateCount());
        dto.setFormat(params.getFormat().getDisplayName());
        dto.setDisplayTimeMs(params.getDisplayTimeMs());
        dto.setDateRangeStart(params.getDateRangeStart());
        dto.setDateRangeEnd(params.getDateRangeEnd());
        dto.setLastResultPercent((int)lastPercent);
        return dto;
    }

    private NamesFacesExerciseParamsResponse buildNamesFacesParamsResponse(
            NamesFacesExerciseParams params, UUID exerciseId, double lastPercent) {

        NamesFacesExerciseParamsResponse dto = new NamesFacesExerciseParamsResponse();
        dto.setExerciseId(exerciseId);
        dto.setNameCount(String.valueOf(params.getNameCount()));
        dto.setNameFormat(params.getNameFormat());
        dto.setDisplayTimeMs(params.getDisplayTimeMs());
        dto.setLastResultPercent(lastPercent);
        return dto;
    }

    private ModuleWorldSetResponse buildWordSetParamsResponse(
            WordSetExerciseParams params, UUID exerciseId, double lastPercent) {

        ModuleWorldSetResponse dto = new ModuleWorldSetResponse();
        dto.setExerciseId(exerciseId);
        dto.setThemeId(params.getTheme().getId());
        dto.setInstantAnswer(params.isInstantFeedback());
        dto.setQuestionCount(params.getQuestionCount());
        dto.setAnswerType(params.getAnswerType().name());
        dto.setLastResultPercent((int)lastPercent);
        return dto;
    }

    // Module creation methods
    private Modules createAndSaveModule(CreateModuleRequest request, Course course) {
        Modules module = new Modules();
        module.setName(request.getModuleName());
        module.setDescription(request.getModuleDescription());
        Optional.ofNullable(request.getEndDate())
                .filter(date -> !date.isBlank())
                .ifPresent(date -> module.setEndDate(LocalDate.parse(date)));
        module.setPassThreshold(request.getPassTresshold());
        module.setCourse(course);
        return moduleRepository.save(module);
    }

    private void createExercisesForModule(Users teacher, Modules module, CreateModuleRequest request) {
        if (request.getNumberExerciseParams() != null) {
            request.getNumberExerciseParams().forEach(params ->
                    createNumberExercise(module, teacher, params));
        }

        if (request.getDateExerciseParams() != null) {
            request.getDateExerciseParams().forEach(params ->
                    createDateExercise(module, teacher, params));
        }

        if (request.getNamesFacesExerciseParams() != null) {
            request.getNamesFacesExerciseParams().forEach(params ->
                    createNamesFacesExercise(module, teacher, params));
        }

        if (request.getModuleWorldSetParams() != null) {
            request.getModuleWorldSetParams().forEach(params ->
                    createWordSetExercise(module, teacher, params));
        }
    }

    private void createNumberExercise(Modules module, Users teacher, NumberExerciseParamsRequest params) {
        Exercise exercise = createBaseExercise(module, teacher, Exercise.ExerciseType.NUMBER);

        NumberExerciseParams entity = new NumberExerciseParams();
        entity.setExercise(exercise);
        entity.setBase(params.getBase());
        entity.setNumberCount(params.getNumberCount());
        entity.setDigitLength(params.getDigit_lenth());
        entity.setMinValue(params.getMinValue());
        entity.setMaxValue(params.getMaxValue());
        entity.setDisplayTimeMs(params.getDisplayTimeMs());
        numberParamsRepository.save(entity);
    }

    private void createDateExercise(Modules module, Users teacher, DateExerciseParamsRequest params) {
        Exercise exercise = createBaseExercise(module, teacher, Exercise.ExerciseType.DATE);

        DateExerciseParams entity = new DateExerciseParams();
        entity.setExercise(exercise);
        entity.setDateCount(params.getDateCount());
        entity.setFormat(DateExerciseParams.DateFormat.fromDisplayName(params.getFormat()));
        entity.setDisplayTimeMs(params.getDisplayTimeMs());
        entity.setDateRangeStart(LocalDate.parse(params.getDateRangeStart()));
        entity.setDateRangeEnd(LocalDate.parse(params.getDateRangeEnd()));
        dateParamsRepository.save(entity);
    }

    private void createNamesFacesExercise(Modules module, Users teacher, NamesFacesExerciseParamsRequest params) {
        Exercise exercise = createBaseExercise(module, teacher, Exercise.ExerciseType.NAME_FACE);

        NamesFacesExerciseParams entity = new NamesFacesExerciseParams();
        entity.setExercise(exercise);
        entity.setNameCount(Integer.parseInt(params.getNameCount()));
        entity.setNameFormat(params.getNameFormat());
        entity.setDisplayTimeMs(params.getDisplayTimeMs());
        namesFacesParamsRepository.save(entity);
    }

    private void createWordSetExercise(Modules module, Users teacher, ModuleWorldSetRequest params) {
        Exercise exercise = createBaseExercise(module, teacher, Exercise.ExerciseType.WORD_SET);

        Theme theme = themeRepository.findById(params.getThemeId())
                .orElseThrow(() -> new RuntimeException("Тема не найдена"));

        WordSetExerciseParams entity = new WordSetExerciseParams();
        entity.setExercise(exercise);
        entity.setTheme(theme);
        entity.setQuestionCount(params.getQuestionCount());
        entity.setInstantFeedback(params.isInstantAnswer());
        entity.setAnswerType(WordSetExerciseParams.AnswerType.valueOf(params.getAnswerType()));
        wordSetParamsRepository.save(entity);
    }

    // Module update methods
    private void updateModuleDetails(Modules module, UpdateModuleRequest request) {
        module.setName(request.getModuleName());
        module.setDescription(request.getModuleDescription());
        module.setEndDate(LocalDate.parse(request.getEndDate()));
        module.setPassThreshold(request.getPassTresshold());
        moduleRepository.save(module);
    }

    private void processExercises(Modules module, Users user, UpdateModuleRequest request,
                                  Map<UUID, Exercise> existingExercises) {

        if (request.getNumberExerciseParams() != null) {
            request.getNumberExerciseParams().forEach(params -> {
                if (params.getExerciseId() != null && existingExercises.containsKey(params.getExerciseId())) {
                    updateNumberExercise(existingExercises.get(params.getExerciseId()), params);
                } else {
                    createNewNumberExercise(module, user, params);
                }
            });
        }

        if (request.getDateExerciseParams() != null) {
            request.getDateExerciseParams().forEach(params -> {
                if (params.getExerciseId() != null && existingExercises.containsKey(params.getExerciseId())) {
                    updateDateExercise(existingExercises.get(params.getExerciseId()), params);
                } else {
                    createNewDateExercise(module, user, params);
                }
            });
        }

        if (request.getNamesFacesExerciseParams() != null) {
            request.getNamesFacesExerciseParams().forEach(params -> {
                if (params.getExerciseId() != null && existingExercises.containsKey(params.getExerciseId())) {
                    updateNamesExercise(existingExercises.get(params.getExerciseId()), params);
                } else {
                    createNewNamesFacesExercise(module, user, params);
                }
            });
        }

        if (request.getModuleWorldSetParams() != null) {
            request.getModuleWorldSetParams().forEach(params -> {
                if (params.getExerciseId() != null && existingExercises.containsKey(params.getExerciseId())) {
                    updateWordExercise(existingExercises.get(params.getExerciseId()), params);
                } else {
                    createNewWordSetExercise(module, user, params);
                }
            });
        }
    }

    private void deleteRemovedExercises(List<Exercise> existingExercises, UpdateModuleRequest request) {
        existingExercises.forEach(exercise -> {
            if (!isExerciseInRequest(exercise, request)) {
                deleteExercise(exercise.getId());
            }
        });
    }

    private boolean isExerciseInRequest(Exercise exercise, UpdateModuleRequest request) {
        if (exercise == null || request == null) return false;

        switch (exercise.getType()) {
            case NUMBER:
                return request.getNumberExerciseParams() != null &&
                        request.getNumberExerciseParams().stream()
                                .anyMatch(p -> p.getExerciseId() != null && p.getExerciseId().equals(exercise.getId()));
            case DATE:
                return request.getDateExerciseParams() != null &&
                        request.getDateExerciseParams().stream()
                                .anyMatch(p -> p.getExerciseId() != null && p.getExerciseId().equals(exercise.getId()));
            case NAME_FACE:
                return request.getNamesFacesExerciseParams() != null &&
                        request.getNamesFacesExerciseParams().stream()
                                .anyMatch(p -> p.getExerciseId() != null && p.getExerciseId().equals(exercise.getId()));
            case WORD_SET:
                return request.getModuleWorldSetParams() != null &&
                        request.getModuleWorldSetParams().stream()
                                .anyMatch(p -> p.getExerciseId() != null && p.getExerciseId().equals(exercise.getId()));
            default:
                logger.warn("Неизвестный тип упражнения: {}", exercise.getType());
                return false;
        }
    }

    private void deleteExercise(UUID exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new NotFoundException("Упражнение не найдено"));

        exerciseResultRepository.deleteAllByExercise(exercise);

        switch (exercise.getType()) {
            case NUMBER: numberParamsRepository.deleteByExercise(exercise); break;
            case DATE: dateParamsRepository.deleteByExercise(exercise); break;
            case NAME_FACE: namesFacesParamsRepository.deleteByExercise(exercise); break;
            case WORD_SET: wordSetParamsRepository.deleteByExercise(exercise); break;
        }

        exerciseRepository.delete(exercise);
    }

    // Exercise update methods
    private void updateNumberExercise(Exercise exercise, UpdateNumberExerciseParamsRequest params) {
        NumberExerciseParams currentParams = numberParamsRepository.findByExercise(exercise)
                .orElseThrow(() -> new NotFoundException("Параметры упражнения не найдены"));

        currentParams.setBase(params.getBase());
        currentParams.setNumberCount(params.getNumberCount());
        currentParams.setDigitLength(params.getDigit_lenth());
        currentParams.setMinValue(params.getMinValue());
        currentParams.setMaxValue(params.getMaxValue());
        numberParamsRepository.save(currentParams);
    }

    private void updateDateExercise(Exercise exercise, UpdateDateExerciseParamsRequest params) {
        DateExerciseParams currentParams = dateParamsRepository.findByExercise(exercise)
                .orElseThrow(() -> new NotFoundException("Параметры упражнения не найдены"));

        currentParams.setDateCount(params.getDateCount());
        currentParams.setFormat(DateExerciseParams.DateFormat.fromDisplayName(params.getFormat()));
        currentParams.setDateRangeEnd(LocalDate.parse(params.getDateRangeEnd()));
        currentParams.setDateRangeStart(LocalDate.parse(params.getDateRangeStart()));
        currentParams.setDisplayTimeMs(params.getDisplayTimeMs());
        dateParamsRepository.save(currentParams);
    }

    private void updateNamesExercise(Exercise exercise, UpdateNamesFacesExerciseParamsRequest params) {
        logger.debug("Обновление упражнения с именами и лицами. Exercise ID: {}", exercise.getId());

        NamesFacesExerciseParams currentParams = namesFacesParamsRepository.findByExercise(exercise)
                .orElseThrow(() -> {
                    logger.error("Параметры упражнения с именами и лицами не найдены. Exercise ID: {}", exercise.getId());
                    return new NotFoundException("Параметры упражнения не найдены");
                });

        currentParams.setDisplayTimeMs(params.getDisplayTimeMs());
        currentParams.setNameCount(params.getNameCount());
        currentParams.setNameFormat(params.getNameFormat());
        namesFacesParamsRepository.save(currentParams);
    }

    private void updateWordExercise(Exercise exercise, UpdateModuleWorldSetRequest params) {
        logger.debug("Обновление упражнения с набором слов. Exercise ID: {}", exercise.getId());

        WordSetExerciseParams currentParams = wordSetParamsRepository.findByExercise(exercise)
                .orElseThrow(() -> {
                    logger.error("Параметры упражнения с набором слов не найдены. Exercise ID: {}", exercise.getId());
                    return new NotFoundException("Параметры упражнения не найдены");
                });

        Theme theme = themeRepository.findById(params.getThemeId())
                .orElseThrow(() -> {
                    logger.error("Тема не найдена. Theme ID: {}", params.getThemeId());
                    return new NotFoundException("Тема не найдена");
                });

        currentParams.setInstantFeedback(params.isInstantAnswer());
        currentParams.setTheme(theme);
        currentParams.setAnswerType(WordSetExerciseParams.AnswerType.valueOf(params.getAnswerType()));
        currentParams.setQuestionCount(params.getQuestionCount());
        wordSetParamsRepository.save(currentParams);
    }

    // Exercise creation methods for update
    private void createNewNumberExercise(Modules module, Users user, UpdateNumberExerciseParamsRequest params) {
        logger.debug("Создание нового числового упражнения для модуля. Module ID: {}", module.getId());
        Exercise exercise = createBaseExercise(module, user, Exercise.ExerciseType.NUMBER);

        NumberExerciseParams entity = new NumberExerciseParams();
        entity.setExercise(exercise);
        entity.setBase(params.getBase());
        entity.setNumberCount(params.getNumberCount());
        entity.setDigitLength(params.getDigit_lenth());
        entity.setMinValue(params.getMinValue());
        entity.setMaxValue(params.getMaxValue());
        entity.setDisplayTimeMs(params.getDisplayTimeMs());
        numberParamsRepository.save(entity);
    }

    private void createNewDateExercise(Modules module, Users user, UpdateDateExerciseParamsRequest params) {
        logger.debug("Создание нового упражнения с датами для модуля. Module ID: {}", module.getId());
        Exercise exercise = createBaseExercise(module, user, Exercise.ExerciseType.DATE);

        DateExerciseParams entity = new DateExerciseParams();
        entity.setExercise(exercise);
        entity.setDateCount(params.getDateCount());
        entity.setFormat(DateExerciseParams.DateFormat.fromDisplayName(params.getFormat()));
        entity.setDisplayTimeMs(params.getDisplayTimeMs());
        entity.setDateRangeStart(LocalDate.parse(params.getDateRangeStart()));
        entity.setDateRangeEnd(LocalDate.parse(params.getDateRangeEnd()));
        dateParamsRepository.save(entity);
    }

    private void createNewNamesFacesExercise(Modules module, Users user, UpdateNamesFacesExerciseParamsRequest params) {
        logger.debug("Создание нового упражнения с именами и лицами для модуля. Module ID: {}", module.getId());

        Exercise exercise = createBaseExercise(module, user, Exercise.ExerciseType.NAME_FACE);

        NamesFacesExerciseParams entity = new NamesFacesExerciseParams();
        entity.setExercise(exercise);
        entity.setNameCount(params.getNameCount());
        entity.setNameFormat(params.getNameFormat());
        entity.setDisplayTimeMs(params.getDisplayTimeMs());
        namesFacesParamsRepository.save(entity);
    }

    private void createNewWordSetExercise(Modules module, Users user, UpdateModuleWorldSetRequest params) {
        logger.debug("Создание нового упражнения с набором слов для модуля. Module ID: {}", module.getId());
        Exercise exercise = createBaseExercise(module, user, Exercise.ExerciseType.WORD_SET);

        Theme theme = themeRepository.findById(params.getThemeId())
                .orElseThrow(() -> {
                    logger.error("Тема не найдена при создании упражнения. Theme ID: {}", params.getThemeId());
                    return new NotFoundException("Тема не найдена");
                });

        WordSetExerciseParams entity = new WordSetExerciseParams();
        entity.setExercise(exercise);
        entity.setTheme(theme);
        entity.setQuestionCount(params.getQuestionCount());
        entity.setInstantFeedback(params.isInstantAnswer());
        entity.setAnswerType(WordSetExerciseParams.AnswerType.valueOf(params.getAnswerType()));
        wordSetParamsRepository.save(entity);
    }

    // Base exercise creation
    private Exercise createBaseExercise(Modules module, Users user, Exercise.ExerciseType type) {
        Exercise exercise = new Exercise();
        exercise.setId(UUID.randomUUID());
        exercise.setModule(module);
        exercise.setType(type);
        exercise.setCreatedBy(user);
        exercise.setCreatedAt(LocalDateTime.now());
        return exerciseRepository.save(exercise);
    }

    // Delete helper methods
    private void deleteExerciseData(List<Exercise> exercises) {
        int numberParamsDeleted = 0;
        int dateParamsDeleted = 0;
        int namesFacesParamsDeleted = 0;
        int wordSetParamsDeleted = 0;

        for (Exercise exercise : exercises) {
            exerciseResultRepository.deleteAllByExercise(exercise);

            switch (exercise.getType()) {
                case NUMBER: numberParamsDeleted += numberParamsRepository.deleteByExercise(exercise); break;
                case DATE: dateParamsDeleted += dateParamsRepository.deleteByExercise(exercise); break;
                case NAME_FACE: namesFacesParamsDeleted += namesFacesParamsRepository.deleteByExercise(exercise); break;
                case WORD_SET: wordSetParamsDeleted += wordSetParamsRepository.deleteByExercise(exercise); break;
            }
        }

        logger.info(ModuleLogMessages.NUMBER_PARAMS_DELETED, numberParamsDeleted);
        logger.info(ModuleLogMessages.DATE_PARAMS_DELETED, dateParamsDeleted);
        logger.info(ModuleLogMessages.NAMES_FACES_PARAMS_DELETED, namesFacesParamsDeleted);
        logger.info(ModuleLogMessages.WORD_SET_PARAMS_DELETED, wordSetParamsDeleted);
    }

    private MessageResponse buildDeleteSuccessMessage(Modules module, int exercisesDeleted) {
        return new MessageResponse("Модуль успешно удален!");
    }
}