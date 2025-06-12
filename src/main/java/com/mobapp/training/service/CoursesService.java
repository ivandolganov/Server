package com.mobapp.training.service;

import com.mobapp.training.dto.request.CourseIdRequest;
import com.mobapp.training.dto.request.CreateCourseRequest;
import com.mobapp.training.dto.request.EditCourseRequest;
import com.mobapp.training.dto.response.*;
import com.mobapp.training.exception.NotFoundException;
import com.mobapp.training.exception.UserHasNoGroupsException;
import com.mobapp.training.logging.CourseLogMessages;
import com.mobapp.training.models.*;
import com.mobapp.training.repo.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CoursesService {

    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final ExerciseResultRepository exerciseResultRepository;
    private final ExerciseRepository exerciseRepository;
    private final GroupRepository groupRepository;
    private final UsersRepository usersRepository;
    private final StudentGroupMemberRepository studentGroupMemberRepository;
    private final CourseGroupRepository courseGroupRepository;
    private final NumberExerciseParamsRepository numberExerciseParamsRepository;
    private final NamesFacesExerciseParamsRepository namesFacesExerciseParamsRepository;
    private final WordSetExerciseParamsRepository wordSetExerciseParamsRepository;
    private final DateExerciseParamsRepository dateExerciseParamsRepository;
    private static final Logger logger = LoggerFactory.getLogger(CoursesService.class);

    @Transactional
    public List<CoursesStudentResponse> getCoursesForStudent(Users student) {
        logger.info(CourseLogMessages.GET_STUDENT_COURSES_START, student.getId());

        Set<StudentGroup> groups = student.getStudentGroups();
        if (groups == null || groups.isEmpty()) {
            logger.warn(CourseLogMessages.STUDENT_HAS_NO_GROUPS, student.getId());
            throw new UserHasNoGroupsException("У пользователя нет групп");
        }

        Set<Course> allCourses = new HashSet<>();

        for (StudentGroup group : groups) {
            List<Course> groupCourses = courseRepository.findByGroupId(group.getId());
            allCourses.addAll(groupCourses);
        }

        List<CoursesStudentResponse> responseList = new ArrayList<>();

        for (Course course : allCourses) {
            List<Modules> modules = moduleRepository.findByCourseId(course.getId());

            List<Exercise> allExercises = new ArrayList<>();
            for (Modules module : modules) {
                List<Exercise> moduleExercises = exerciseRepository.findByModuleId(module.getId());
                allExercises.addAll(moduleExercises);
            }

            double avgResult = 0.0;
            if (!allExercises.isEmpty()) {
                double totalScore = 0.0;

                for (Exercise exercise : allExercises) {
                    Optional<ExerciseResult> resultOpt = exerciseResultRepository
                            .findTopByExerciseAndStudentOrderByAttemptedAtDesc(exercise, student);

                    double percent = resultOpt
                            .map(r -> (double) r.getCorrectCount() / r.getTotalCount())
                            .orElse(0.0);

                    totalScore += percent * 100.0; // в процентах
                }

                avgResult = totalScore / allExercises.size();
            }
            logger.debug(CourseLogMessages.CALCULATE_AVG_RESULT,
                    course.getId(), String.format("%.2f", avgResult));
            CoursesStudentResponse cr = new CoursesStudentResponse();
            cr.setCourseId(course.getId());
            cr.setCourseTitle(course.getName());
            cr.setModulesCount(modules.size());
            cr.setTeacherName(course.getTeacher().getFullName());
            cr.setAverageResult((int) avgResult);

            responseList.add(cr);
        }

        return responseList;
    }

    @Transactional
    public List<CoursesTeacherResponse> getCoursesForTeacher(Users teacher) {
        logger.info(CourseLogMessages.GET_TEACHER_COURSES_START, teacher.getId());

        Set<Course> courses = teacher.getTeacherCourses().stream()
                .peek(course -> Hibernate.initialize(course.getCoursesGroups()))
                .collect(Collectors.toSet());

        if (courses.isEmpty()) {
            logger.debug(CourseLogMessages.TEACHER_HAS_NO_COURSES, teacher.getId());
        }

        return courses.stream()
                .map(course -> {
                    CoursesTeacherResponse response = new CoursesTeacherResponse();
                    response.setCourseId(course.getId());
                    response.setCourseTitle(course.getName());
                    response.setModulesCount(course.getModuleCount());
                    response.setGroups(
                            course.getCoursesGroups().stream()
                                    .map(StudentGroup::getName)
                                    .collect(Collectors.toList())
                    );
                    return response;
                })
                .collect(Collectors.toList());
    }

    public MessageResponse createCourse(Users user, CreateCourseRequest request) {
        // Создаем курс

        Course course = new Course();
        course.setTeacher(user);
        course.setName(request.getCourseName());

        // Добавляем группы (оптимизированная версия)
        Set<StudentGroup> groups = groupRepository.findAllByIdIn(request.getGroupsId());
        if (groups.size() != request.getGroupsId().size()) {
            throw new NotFoundException("Некоторые группы не найдены");
        }
        logger.debug(CourseLogMessages.CREATE_COURSE_VALIDATION,
                request.getGroupsId().size(), groups.size());

        course.setCoursesGroups(groups);
        courseRepository.save(course);
        logger.info(CourseLogMessages.COURSE_CREATED,
                course.getId(), course.getName(), groups.size());
        return new MessageResponse("Курс успешно создан");
    }


    @Transactional
    public EditCourseResponse editCourse(UUID courseId) {
        // Проверяем существование преподавателя
        logger.info(CourseLogMessages.EDIT_COURSE_START, courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Курс с id = " + courseId + "не найден"));

        Set<StudentGroup> groups = course.getCoursesGroups();

        EditCourseResponse resp = new EditCourseResponse();

        List<GroupResponse> groupResponses = new ArrayList<>();
        for (StudentGroup group : groups) {
            GroupResponse groupResp = new GroupResponse();
            groupResp.setGroupId(group.getId());
            groupResp.setGroupName(group.getName());
            groupResp.setGroupCode(group.getCode());
            groupResp.setGroupMembersCount(studentGroupMemberRepository.countByGroupId(group.getId()));
            groupResponses.add(groupResp);
        }
        resp.setCourseId(course.getId());
        resp.setGroups(groupResponses);
        resp.setCourseName(course.getName());

        logger.debug(CourseLogMessages.EDIT_COURSE_GROUPS,
                courseId, groups.size());

        return resp;
    }

    @Transactional
    public MessageResponse saveCourseChanges(UUID courseId, EditCourseRequest request) {
        logger.info(CourseLogMessages.SAVE_COURSE_START, courseId);

        // 1. Находим курс
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Курс не найден"));

        // 2. Обновляем имя (если изменилось)
        if (!course.getName().equals(request.getCourseName())) {
            logger.info(CourseLogMessages.COURSE_NAME_UPDATED,
                    courseId, course.getName(), request.getCourseName());
            course.setName(request.getCourseName());
        }

        // 3. Получаем текущие группы курса
        Set<StudentGroup> studentGroups = course.getCoursesGroups();
        Set<UUID> currentGroupIds = studentGroups.stream()
                .map(StudentGroup::getId)
                .collect(Collectors.toSet());

        // 4. Получаем новые группы из запроса
        Set<UUID> newGroupIds = new HashSet<>(request.getGroupsId());

        // 5. Находим группы для добавления
        Set<UUID> groupsToAdd = newGroupIds.stream()
                .filter(id -> !currentGroupIds.contains(id))
                .collect(Collectors.toSet());

        // 6. Находим группы для удаления
        Set<UUID> groupsToRemove = currentGroupIds.stream()
                .filter(id -> !newGroupIds.contains(id))
                .collect(Collectors.toSet());

        // 7. Добавляем новые группы
        if (!groupsToAdd.isEmpty()) {
            List<StudentGroup> groups = groupRepository.findAllById(groupsToAdd);
            logger.info(CourseLogMessages.COURSE_GROUPS_ADDED,
                    courseId, groupsToAdd);
            course.getCoursesGroups().addAll(groups);
        }

        // 8. Удаляем старые группы
        if (!groupsToRemove.isEmpty()) {
            logger.info(CourseLogMessages.COURSE_GROUPS_REMOVED,
                    courseId, groupsToRemove);
            course.getCoursesGroups().removeIf(group -> groupsToRemove.contains(group.getId()));
        }

        // 9. Сохраняем изменения
        courseRepository.save(course);

        return new MessageResponse("Курс успешно обновлен");
    }

    @Transactional
    public MessageResponse deleteCourse(UUID userId, UUID courseId) {
        logger.info(CourseLogMessages.DELETE_COURSE_VALIDATION, courseId, userId);

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Курс не найден"));

        // Удаление всех связей курс-группа
        courseGroupRepository.deleteAllByCourse(course);

        // Удаление всех модулей и их содержимого
        List<Modules> modules = moduleRepository.findByCourseId(course.getId());
        logger.debug(CourseLogMessages.DELETE_COURSE_MODULES,
                courseId, modules.size());
        modules.forEach(module -> {
            List<Exercise> exercises = exerciseRepository.findByModule(module);
            logger.debug(CourseLogMessages.DELETE_COURSE_EXERCISES,
                    module.getId(), exercises.size());
            exercises.forEach(exercise -> {
                numberExerciseParamsRepository.deleteAllByExercise(exercise);
                namesFacesExerciseParamsRepository.deleteAllByExercise(exercise);
                wordSetExerciseParamsRepository.deleteAllByExercise(exercise);
                dateExerciseParamsRepository.deleteAllByExercise(exercise);
                exerciseResultRepository.deleteAllByExercise(exercise);
            });

            exerciseRepository.deleteAllByModule(module);
            moduleRepository.delete(module);
        });

        courseRepository.delete(course);
        logger.info(CourseLogMessages.COURSE_DELETED, courseId);
        return new MessageResponse("Курс успешно удалён");
    }
}