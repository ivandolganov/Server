package com.mobapp.training.service;


import com.mobapp.training.dto.request.CreateGroupRequest;
import com.mobapp.training.dto.request.DeleteGroupRequest;
import com.mobapp.training.dto.request.EditGroupRequest;
import com.mobapp.training.dto.response.*;
import com.mobapp.training.exception.NotFoundException;
import com.mobapp.training.logging.GroupLogMessages;
import com.mobapp.training.models.*;
import com.mobapp.training.repo.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final StudentGroupMemberRepository studentGroupMemberRepository;
    private final UsersRepository usersRepository;
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseResultRepository exerciseResultRepository;
    private final CourseGroupRepository courseGroupRepository;
    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);

    @Transactional
    public List<AllGroupResponse> getAllGroups(Users user) {
        logger.info(GroupLogMessages.GET_ALL_GROUPS);

        List<AllGroupResponse> groupsResponse = new ArrayList<>();

        groupRepository.findByCreator(user).forEach(group -> {
            AllGroupResponse gr = new AllGroupResponse();
            gr.setGroupId(group.getId());
            gr.setGroupName(group.getName());
            gr.setGroupCode(group.getCode());
            gr.setGroupMembersCount(studentGroupMemberRepository.countByGroupId(group.getId()));

            // Для каждой группы находим курс (если есть)
            List<Course> courses = courseRepository.findByGroupId(group.getId());

            // Берем первый курс, если список не пустой
            gr.setCourseName(!courses.isEmpty() ? courses.get(0).getName() : null);

            groupsResponse.add(gr);
        });

        logger.info(GroupLogMessages.FOUND_ALL_GROUPS, groupsResponse.size());
        return groupsResponse;
    }

    @Transactional
    public GroupsResponse getGroupsNotInCourse(Users user) {
        logger.info(GroupLogMessages.GET_USER_GROUPS_NOT_IN_ANY_COURSE, user.getId());

        // 1. Получаем все группы пользователя
        List<StudentGroup> userGroups = groupRepository.findByCreator(user);

        // 2. Получаем ID всех групп, привязанных к любым курсам
        Set<UUID> groupsInCourses = courseGroupRepository.findAllGroupIdsInAnyCourse();

        // 3. Фильтруем - оставляем только группы пользователя, не привязанные к курсам
        List<StudentGroup> userGroupsNotInAnyCourse = userGroups.stream()
                .filter(group -> !groupsInCourses.contains(group.getId()))
                .collect(Collectors.toList());

        // 4. Формируем ответ
        GroupsResponse response = new GroupsResponse();
        userGroupsNotInAnyCourse.forEach(group -> {
            GroupResponse groupResponse = new GroupResponse();
            groupResponse.setGroupId(group.getId());
            groupResponse.setGroupName(group.getName());
            groupResponse.setGroupCode(group.getCode());
            groupResponse.setGroupMembersCount(
                    studentGroupMemberRepository.countByGroupId(group.getId())
            );
            response.getGroups().add(groupResponse);
        });

        logger.info(GroupLogMessages.FOUND_USER_GROUPS_NOT_IN_ANY_COURSE,
                response.getGroups().size(), user.getId());
        return response;
    }

    public MessageResponse createGroup(CreateGroupRequest request, Users user) {
        String groupCode;
        do {
            groupCode = generateRandomCode(4);
        } while (groupRepository.existsByCode(groupCode));

        StudentGroup group = new StudentGroup();
        group.setName(request.getGroupName());
        group.setCode(groupCode);
        group.setCreator(user);
        groupRepository.save(group);

        logger.info(GroupLogMessages.CREATE_GROUP_FINAL, request.getGroupName(), groupCode);
        return new MessageResponse("Группа успешно создана!");
    }

//    @Transactional
//    public List<GroupCourseResponse> getGroupsByTeacher(UUID teacherId) {
//        logger.info(GroupLogMessages.GET_GROUPS_BY_TEACHER, teacherId);
//        List<Course> courses = courseRepository.findByTeacherId(teacherId);
//        List<GroupCourseResponse> responses = new ArrayList<>();
//
//        for (Course course : courses) {
//            logger.debug(GroupLogMessages.PROCESSING_COURSE , course.getName(), course.getId());
//            List<StudentGroup> groups = groupRepository.findGroupsByCourseId(course.getId());
//
//            for (StudentGroup group : groups) {
//                responses.add(new GroupCourseResponse(
//                        course.getId(),
//                        course.getName(),
//                        group.getId(),
//                        group.getName(),
//                        group.getCode(),
//                        studentGroupMemberRepository.countByGroupId(group.getId())
//                ));
//            }
//        }
//
//        logger.info(GroupLogMessages.FOUND_TEACHER_GROUPS , responses.size());
//        return responses;
//    }

    @Transactional
    public MessageResponse deleteGroup(DeleteGroupRequest request) {
        UUID groupId = request.getGroupId();
        logger.info(GroupLogMessages.GROUP_DELETE_START, groupId);

        StudentGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> {
                    logger.error(GroupLogMessages.GROUP_NOT_FOUND, groupId);
                    return new NotFoundException("Группа не найдена");
                });

        // Удаление связей с курсами
        int coursesCount = courseGroupRepository.deleteAllByGroupId(groupId);
        logger.info(GroupLogMessages.COURSES_UNLINKED, coursesCount);

        // Удаление связей со студентами
        int studentsCount = group.getStudents().size();
        group.getStudents().clear(); // Hibernate удалит записи из join-таблицы
        logger.info(GroupLogMessages.STUDENTS_UNLINKED, studentsCount);

        // Удаление группы
        groupRepository.delete(group);
        logger.info(GroupLogMessages.GROUP_DELETED, groupId);

        return new MessageResponse("Группа успешно удалена!");
    }

    @Transactional
    public MessageResponse editGroup(EditGroupRequest request) {
        UUID groupId = request.getGroupId();

        // 1. Находим группу
        StudentGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> {
                    logger.error(GroupLogMessages.GROUP_NOT_FOUND, groupId);
                    return new NotFoundException("Группа не найдена");
                });

        // 5. Обновляем другие поля группы
        if (request.getGroupName() != null && !request.getGroupName().isBlank()) {
            group.setName(request.getGroupName());
        }

        groupRepository.save(group);

        return new MessageResponse("Группа успешно обновлена!");
    }

    @Transactional
    public GroupMembersResponse getStudentsByGroupId(UUID groupId) {
        StudentGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Группа не найдена"));

        List<StudentResponse> students = group.getStudents().stream()
                .map(s -> new StudentResponse(s.getId(), s.getFullName()))
                .collect(Collectors.toList());

        GroupMembersResponse response = new GroupMembersResponse();
        response.setGroupId(group.getId());
        response.setGroupName(group.getName());
        response.setGroupCode(group.getCode());
        response.setMembersCount(students.size());
        response.setStudents(students);

        List<Course> courses = courseRepository.findByGroupId(group.getId());

        // Берем первый курс, если список не пустой
        response.setCourseName(!courses.isEmpty() ? courses.get(0).getName() : null);

        return response;
    }

    @Transactional
    public StudentCourseStatsResponse getStudentStatsForGroup(UUID studentId, UUID groupId) {
        logger.info(GroupLogMessages.GET_STUDENT_STATS, studentId, groupId);

        // Получаем студента
        Users student = usersRepository.findById(studentId)
                .orElseThrow(() -> {
                    logger.warn(GroupLogMessages.STUDENT_NOT_FOUND, studentId);
                    return new NotFoundException("Студент не найден");
                });

        // Получаем курс по ID группы
        Course course = courseRepository.findByGroupId(groupId)
                .stream()
                .findFirst()
                .orElseThrow(() -> {
                    logger.warn(GroupLogMessages.COURSE_NOT_FOUND_FOR_GROUP, groupId);
                    return new NotFoundException("Курс не найден для указанной группы");
                });

        UUID courseId = course.getId();

        List<Modules> modules = moduleRepository.findByCourseId(courseId);
        logger.debug(GroupLogMessages.MODULES_FOUND , modules.size());

        double courseScoreSum = 0.0;
        int totalExercisesCount = 0;
        List<StudentCourseStatsResponse.ModuleStats> moduleStatsList = new ArrayList<>();

        for (Modules module : modules) {
            List<Exercise> exercises = exerciseRepository.findByModule(module);

            double moduleScoreSum = 0.0;
            List<StudentCourseStatsResponse.ExerciseStats> exerciseStatsList = new ArrayList<>();

            for (Exercise exercise : exercises) {
                Optional<ExerciseResult> resultOpt = exerciseResultRepository
                        .findTopByExerciseAndStudentOrderByAttemptedAtDesc(exercise, student);

                double percent = resultOpt
                        .map(r -> (r.getTotalCount() > 0)
                                ? ((double) r.getCorrectCount() / r.getTotalCount()) * 100.0
                                : 0.0)
                        .orElse(0.0);

                StudentCourseStatsResponse.ExerciseStats exStats = new StudentCourseStatsResponse.ExerciseStats();
                exStats.setExerciseId(exercise.getId());
                exStats.setLastAttemptPercent((int)percent);
                exStats.setExerciseType(exercise.getType().name());
                exerciseStatsList.add(exStats);

                moduleScoreSum += percent;
            }

            int exerciseCount = exercises.size();
            totalExercisesCount += exerciseCount;
            courseScoreSum += moduleScoreSum;

            StudentCourseStatsResponse.ModuleStats moduleStats = new StudentCourseStatsResponse.ModuleStats();
            moduleStats.setModuleId(module.getId());
            moduleStats.setModuleName(module.getName());
            moduleStats.setModuleAverage((int)(exerciseCount > 0 ? moduleScoreSum / exerciseCount : 0.0));
            moduleStats.setExercises(exerciseStatsList);
            moduleStatsList.add(moduleStats);
        }

        StudentCourseStatsResponse response = new StudentCourseStatsResponse();
        response.setUserFulName(student.getFullName());
        response.setCourseId(course.getId());
        response.setCourseTitle(course.getName());
        response.setCourseAverage((int)(totalExercisesCount > 0 ? courseScoreSum / totalExercisesCount : 0.0));
        response.setModules(moduleStatsList);

        logger.info(GroupLogMessages.COURSE_AVERAGE_CALCULATED , response.getCourseAverage());
        return response;
    }

    @Transactional
    public MessageResponse deleteStudentFromGroup(UUID studentId, UUID groupId) {
        // Находим студента и группу
        Users student = usersRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        StudentGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Группа не найдена"));

        // Проверяем, состоит ли студент в этой группе
        if (!student.getStudentGroups().contains(group)) {
            throw new NotFoundException("Student is not a member of this group");
        }

        // Удаляем связь
        student.getStudentGroups().remove(group);
        group.getStudents().remove(student); // Если есть обратная связь

        // Сохраняем изменения (не обязательно с @Transactional)
        usersRepository.save(student);
        groupRepository.save(group);

        // Возвращаем статистику (заглушка, адаптируйте под ваш DTO)
        return new MessageResponse("Студент удален из группы!");
    }

//    @Transactional
//    public GroupCourseStatsResponse getGroupStatsForCourse(UUID groupId) {
//        logger.info(GroupLogMessages.GET_GROUP_STATS, groupId);
//
//        // Получаем группу
//        StudentGroup group = groupRepository.findById(groupId)
//                .orElseThrow(() -> {
//                    logger.warn(GroupLogMessages.GROUP_NOT_FOUND, groupId);
//                    return new NotFoundException("Группа не найдена");
//                });
//
//        // Получаем курс по ID группы
//        Course course = courseRepository.findByGroupId(groupId)
//                .stream()
//                .findFirst()
//                .orElseThrow(() -> {
//                    logger.warn(GroupLogMessages.COURSE_NOT_FOUND_FOR_GROUP, groupId);
//                    return new NotFoundException("Курс не найден для указанной группы");
//                });
//
//        UUID courseId = course.getId();
//
//        List<Modules> modules = moduleRepository.findByCourseId(courseId);
//        List<Exercise> allExercises = modules.stream()
//                .flatMap(m -> exerciseRepository.findByModule(m).stream())
//                .toList();
//
//        Set<Users> students = group.getStudents();
//        List<GroupCourseStatsResponse.StudentStats> studentStatsList = new ArrayList<>();
//
//        for (Users student : students) {
//            double courseScoreSum = 0.0;
//            int totalExerciseCount = 0;
//            List<GroupCourseStatsResponse.ModuleStats> moduleStatsList = new ArrayList<>();
//
//            for (Modules module : modules) {
//                List<Exercise> exercises = exerciseRepository.findByModule(module);
//                double moduleScoreSum = 0.0;
//                List<GroupCourseStatsResponse.ExerciseStats> exerciseStatsList = new ArrayList<>();
//
//                for (Exercise exercise : exercises) {
//                    Optional<ExerciseResult> resultOpt = exerciseResultRepository
//                            .findTopByExerciseAndStudentOrderByAttemptedAtDesc(exercise, student);
//
//                    double percent = resultOpt
//                            .map(r -> (r.getTotalCount() > 0)
//                                    ? ((double) r.getCorrectCount() / r.getTotalCount()) * 100.0
//                                    : 0.0)
//                            .orElse(0.0);
//
//                    LocalDateTime attemptTime = resultOpt.map(ExerciseResult::getAttemptedAt).orElse(null);
//
//                    GroupCourseStatsResponse.ExerciseStats exStats = new GroupCourseStatsResponse.ExerciseStats();
//                    exStats.setExerciseId(exercise.getId());
//                    exStats.setExerciseType(exercise.getType().name());
//                    exStats.setLastAttemptPercent(percent);
//                    exStats.setLastAttemptTime(attemptTime);
//                    exerciseStatsList.add(exStats);
//
//                    moduleScoreSum += percent;
//                }
//
//                int moduleExerciseCount = exercises.size();
//                courseScoreSum += moduleScoreSum;
//                totalExerciseCount += moduleExerciseCount;
//
//                GroupCourseStatsResponse.ModuleStats moduleStats = new GroupCourseStatsResponse.ModuleStats();
//                moduleStats.setModuleId(module.getId());
//                moduleStats.setModuleName(module.getName());
//                moduleStats.setModuleAverage(moduleExerciseCount > 0 ? moduleScoreSum / moduleExerciseCount : 0.0);
//                moduleStats.setPassThreshold(module.getPassThreshold());
//                moduleStats.setExercises(exerciseStatsList);
//                moduleStatsList.add(moduleStats);
//            }
//
//            GroupCourseStatsResponse.StudentStats studentStats = new GroupCourseStatsResponse.StudentStats();
//            studentStats.setModules(moduleStatsList);
//            studentStatsList.add(studentStats);
//        }
//
//        GroupCourseStatsResponse response = new GroupCourseStatsResponse();
//        response.setGroupId(group.getId());
//        response.setGroupCode(group.getCode());
//        response.setGroupTitle(group.getName());
//        response.setCountMember(studentGroupMemberRepository.countByGroupId(group.getId()));
//        response.setCourseTitle(course.getName());
//        response.setStudents(studentStatsList);
//
//        logger.info(GroupLogMessages.GROUP_STATS_CALCULATED , groupId, courseId);
//        return response;
//    }

    @Transactional
    public GroupCourseStatsResponse getGroupStatsForCourse(UUID groupId) {
        logger.info(GroupLogMessages.GET_GROUP_STATS, groupId);

        // Получаем группу и курс (оптимизированный запрос)
        StudentGroup group = groupRepository.findWithStudentsById(groupId)
                .orElseThrow(() -> {
                    logger.warn(GroupLogMessages.GROUP_NOT_FOUND, groupId);
                    return new NotFoundException("Группа не найдена");
                });

        Course course = courseRepository.findByGroupId(groupId)
                .stream()
                .findFirst()
                .orElseThrow(() -> {
                    logger.warn(GroupLogMessages.COURSE_NOT_FOUND_FOR_GROUP, groupId);
                    return new NotFoundException("Курс не найден для указанной группы");
                });

        // Основной объект ответа
        GroupCourseStatsResponse response = new GroupCourseStatsResponse();
        response.setGroupId(group.getId());
        response.setGroupCode(group.getCode());
        response.setGroupTitle(group.getName());
        response.setCountMember(group.getStudents().size());
        response.setCourseTitle(course.getName());

        // Получаем все модули и упражнения за один запрос
        List<Modules> modules = moduleRepository.findWithExercisesByCourseId(course.getId());

        // Собираем статистику по модулям
        List<GroupCourseStatsResponse.ModuleStats> moduleStatsList = modules.stream()
                .map(module -> processModuleStats(module, group.getStudents()))
                .collect(Collectors.toList());

        response.setModules(moduleStatsList);
        logger.info(GroupLogMessages.GROUP_STATS_CALCULATED, groupId, course.getId());
        return response;
    }

    private GroupCourseStatsResponse.ModuleStats processModuleStats(Modules module, Set<Users> students) {
        GroupCourseStatsResponse.ModuleStats moduleStats = new GroupCourseStatsResponse.ModuleStats();
        moduleStats.setModuleName(module.getName());
        moduleStats.setPassThreshold(module.getPassThreshold());

        // Получаем все упражнения модуля
        List<Exercise> allExercises = module.getExercises();
        List<Exercise> lexicalExercises = allExercises.stream()
                .filter(e -> e.getType() == Exercise.ExerciseType.WORD_SET)
                .collect(Collectors.toList());

        // Для каждого студента вычисляем прогресс
        List<Integer> studentProgress = new ArrayList<>();
        List<Integer> studentLexProgress = new ArrayList<>();

        for (Users student : students) {
            studentProgress.add(calculateAverageResult(allExercises, student));
            studentLexProgress.add(calculateAverageResult(lexicalExercises, student));
        }

        moduleStats.setStudentModuleProgress(studentProgress);
        moduleStats.setStudentModuleLexProgress(studentLexProgress);

        return moduleStats;
    }

    private int calculateAverageResult(List<Exercise> exercises, Users student) {
        if (exercises.isEmpty()) return 0;

        // Используем безопасное деление
        return (int) exercises.stream()
                .mapToInt(exercise -> getExerciseResultPercent(exercise, student))
                .average()
                .orElse(0);
    }

    private int getExerciseResultPercent(Exercise exercise, Users student) {
        return exerciseResultRepository
                .findTopByExerciseAndStudentOrderByAttemptedAtDesc(exercise, student)
                .map(r -> {
                    if (r.getTotalCount() == 0) return 0; // Защита от деления на 0
                    return (r.getCorrectCount() * 100) / r.getTotalCount();
                })
                .orElse(0);
    }


    private String generateRandomCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }

        return sb.toString();
    }
}
