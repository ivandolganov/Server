//package com.mobapp.training.service;
//
//import com.mobapp.training.dto.request.ModuleRequest;
//import com.mobapp.training.dto.response.LoginResponse;
//import com.mobapp.training.dto.response.ModuleResponse;
//import com.mobapp.training.dto.response.ModulesResponse;
//import com.mobapp.training.exception.*;
//import com.mobapp.training.models.*;
//import com.mobapp.training.repo.CourseGroupRepository;
//import com.mobapp.training.repo.CourseRepository;
//import com.mobapp.training.repo.ModuleRepository;
//import com.mobapp.training.repo.UsersRepository;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import jakarta.transaction.Transactional;
//
//import java.time.LocalDate;
//import java.util.*;
//
//@Service
//public class ModuleService {
//
//    private final UsersRepository usersRepository;
//    private final CourseGroupRepository courseGroupRepository;
//    private final ModuleRepository moduleRepository;
//    private final CourseRepository courseRepository;
//
//    public ModuleService(UsersRepository usersRepository,
//                         CourseGroupRepository courseGroupRepository,
//                         ModuleRepository moduleRepository, CourseRepository courseRepository) {
//        this.usersRepository = usersRepository;
//        this.courseGroupRepository = courseGroupRepository;
//        this.moduleRepository = moduleRepository;
//        this.courseRepository = courseRepository;
//    }
//
//    @Transactional
//    public List<ModuleResponse> getModuleForStudent(ModuleRequest request) {
//        Users student = usersRepository.findById(request.getStudentId())
//                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
//
//        Set<StudentGroup> groups = student.getStudentGroups();
//        if (groups == null || groups.isEmpty()) {
//            throw new UserHasNoGroupsException("У пользователя нет групп");
//        }
//
//        Modules module = moduleRepository.findById(request.getModuleId())
//                .orElseThrow(() -> new ModuleNotFoundException("Модуль не найден"));
//
//        Course course = courseRepository.findByModuleId(request.getModuleId())
//                .orElseThrow(() -> new CourseNotFoundException("Курс для модуля не найден"));
//
//        boolean hasAccess = courseGroupRepository.existsByCourseIdAndGroupIn(course.getId(), groups);
//        if (!hasAccess) {
//            throw new AccessDeniedException("У пользователя нет доступа к модулю");
//        }
//
//        String teacherName = course.getTeacher().getFullName();
//
//        ModuleResponse resp = new ModuleResponse();
//        resp.setModuleId(module.getId());
//        resp.setModuleTitle(module.getName());
//        resp.setDescription(module.getDescription());
//        resp.setPassThreshold(module.getPassThreshold());
//        resp.setEndDate(module.getEndDate());
//        resp.setTeacherName(teacherName);
//        // Получаем упражнения
//        List<Exercise> exercises = exerciseRepository.findByModule(module);
//
//        // Фильтрация и преобразование по типам
//        List<NumberExerciseParamsRequest> numberParams = new ArrayList<>();
//        List<DateExerciseParamsRequest> dateParams = new ArrayList<>();
//        List<NamesFacesExerciseParamsRequest> nameFaceParams = new ArrayList<>();
//        List<ModuleWorldSetRequest> wordSetParams = new ArrayList<>();
//
//        for (Exercise ex : exercises) {
//            switch (ex.getType()) {
//                case NUMBER:
//                    numberParams.add(
//                            numberParamsRepository.findByExercise(ex)
//                                    .map(e -> {
//                                        NumberExerciseParamsRequest dto = new NumberExerciseParamsRequest();
//                                        dto.setBase(e.getBase());
//                                        dto.setNumberCount(e.getNumberCount());
//                                        dto.setDigit_lenth(e.getDigitLength());
//                                        dto.setMinValue(e.getMinValue());
//                                        dto.setMaxValue(e.getMaxValue());
//                                        dto.setDisplayTimeMs(e.getDisplayTimeMs());
//                                        return dto;
//                                    }).orElse(null)
//                    );
//                    break;
//
//                case DATE:
//                    dateParams.add(
//                            dateParamsRepository.findByExercise(ex)
//                                    .map(e -> {
//                                        DateExerciseParamsRequest dto = new DateExerciseParamsRequest();
//                                        dto.setDateCount(e.getDateCount());
//                                        dto.setFormat(e.getFormat());
//                                        dto.setDisplayTimeMs(e.getDisplayTimeMs());
//                                        dto.setDateRangeStart(e.getDateRangeStart().toString());
//                                        dto.setDateRangeEnd(e.getDateRangeEnd().toString());
//                                        return dto;
//                                    }).orElse(null)
//                    );
//                    break;
//
//                case NAME_FACE:
//                    nameFaceParams.add(
//                            namesFacesParamsRepository.findByExercise(ex)
//                                    .map(e -> {
//                                        NamesFacesExerciseParamsRequest dto = new NamesFacesExerciseParamsRequest();
//                                        dto.setNameCount(String.valueOf(e.getNameCount()));
//                                        dto.setNameFormat(e.getNameFormat());
//                                        dto.setDisplayTimeMs(e.getDisplayTimeMs());
//                                        return dto;
//                                    }).orElse(null)
//                    );
//                    break;
//
//                case WORD_SET:
//                    wordSetParams.add(
//                            wordSetParamsRepository.findByExercise(ex)
//                                    .map(e -> {
//                                        ModuleWorldSetRequest dto = new ModuleWorldSetRequest();
//                                        dto.setThemeId(e.getTheme().getId());
//                                        dto.setInstantAnswer(e.isInstantFeedback());
//                                        dto.setQuestionCount(e.getQuestionCount());
//                                        dto.setAnswerType(e.getAnswerType().name());
//                                        return dto;
//                                    }).orElse(null)
//                    );
//                    break;
//            }
//        }
//
//        resp.setNumberExerciseParams(numberParams.stream().filter(Objects::nonNull).toList());
//        resp.setDateExerciseParams(dateParams.stream().filter(Objects::nonNull).toList());
//        resp.setNamesFacesExerciseParams(nameFaceParams.stream().filter(Objects::nonNull).toList());
//        resp.setModuleWorldSetParams(wordSetParams.stream().filter(Objects::nonNull).toList());
//
//        return List.of(resp);
//    }
//}