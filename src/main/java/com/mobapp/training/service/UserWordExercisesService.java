package com.mobapp.training.service;

import com.mobapp.training.dto.request.*;
import com.mobapp.training.dto.response.*;
import com.mobapp.training.exception.*;
import com.mobapp.training.logging.UserWordExercisesLogMessages;
import com.mobapp.training.models.*;
import com.mobapp.training.repo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class UserWordExercisesService {

    private static final Logger logger = LoggerFactory.getLogger(UserWordExercisesService.class);
    private final WordThemeRepository themeRepository;
    private final UserWordRepository wordRepository;
    private final WordThemeResultRepository resultRepository;
    private final UsersRepository usersRepository;

    public UUID createTheme(UUID userId, WordThemeRequest request) {
        logger.info(UserWordExercisesLogMessages.THEME_CREATION_START, userId, request.getTitle());

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error(UserWordExercisesLogMessages.USER_NOT_FOUND, userId);
                    return new NotFoundException("Пользователь не найден");
                });

        WordTheme theme = new WordTheme();

        theme.setTitle(request.getTitle());
        theme.setUser(user);

        WordTheme savedTheme = themeRepository.save(theme);

        request.getWords().stream()
                .map(wordData -> {
                    UserWord word = new UserWord();
                    word.setTheme(theme);
                    word.setTerm(wordData.getTerm());
                    word.setTranslation(wordData.getTranslation());
                    word.setTranscription(wordData.getTranscription());
                    return word;
                })
                .forEach(wordRepository::save);

        logger.info(UserWordExercisesLogMessages.THEME_CREATED, savedTheme.getId(), request.getWords().size());

        return savedTheme.getId();
    }

    public MessageResponse addWordsToTheme(UserWordRequest request, UUID themeId) {
        logger.info(UserWordExercisesLogMessages.ADD_WORDS_START, themeId, request.getWords().size());

        WordTheme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> {
                    logger.error(UserWordExercisesLogMessages.THEME_NOT_FOUND, themeId);
                    return new NotFoundException("Тема не найдена");
                });

        request.getWords().stream()
                .map(wordData -> {
                    UserWord word = new UserWord();
                    word.setTheme(theme);
                    word.setTerm(wordData.getTerm());
                    word.setTranslation(wordData.getTranslation());
                    word.setTranscription(wordData.getTranscription());
                    return word;
                })
                .forEach(wordRepository::save);

        logger.info(UserWordExercisesLogMessages.WORDS_ADDED, themeId);

        return new MessageResponse("Слова успешно сохранены");
    }

    public MessageResponse saveResult(UUID userId, UUID themeId, WordThemeResultRequest request) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error(UserWordExercisesLogMessages.USER_NOT_FOUND, userId);
                    return new NotFoundException("Пользователь не найден");
                });

        WordTheme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> {
                    logger.error(UserWordExercisesLogMessages.THEME_NOT_FOUND, themeId);
                    return new NotFoundException("Набор не найден");
                });

        WordThemeResult result = new WordThemeResult();
        result.setUser(user);
        result.setTheme(theme);
        result.setResultTime(request.getResultTime());

        resultRepository.save(result);

        logger.info(UserWordExercisesLogMessages.RESULT_SAVED, userId, themeId);


        return new MessageResponse("Результат успешно сохранен!");
    }

    public List<WordThemeResponse> getUserThemes(UUID userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error(UserWordExercisesLogMessages.USER_NOT_FOUND, userId);
                    return new NotFoundException("Пользователь не найден");
                });

        List<WordTheme> themes = themeRepository.findByUser(user);

        logger.info(UserWordExercisesLogMessages.THEMES_RETRIEVED, userId, themes.size());

        return themes.stream().map(theme -> {
            WordThemeResponse response = new WordThemeResponse();
            response.setThemeId(theme.getId());
            response.setTitle(theme.getTitle());

            resultRepository.findTopByUserAndThemeOrderByAttemptedAtDesc(user, theme)
                    .ifPresent(r -> response.setLastResultTime((int)r.getResultTime()));

            return response;
        }).toList();
    }

    public UserThemeWordsResponse getUsersThemeWords(UUID themeId, UUID userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error(UserWordExercisesLogMessages.USER_NOT_FOUND, userId);
                    return new NotFoundException("Пользователь не найден");
                });

        WordTheme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> {
                    logger.error(UserWordExercisesLogMessages.THEME_NOT_FOUND, themeId);
                    return new NotFoundException("Набор не найден");
                });

        UserThemeWordsResponse response = new UserThemeWordsResponse();
        response.setThemeId(theme.getId());
        response.setTitle(theme.getTitle());

        List<UserWordResponse> words = wordRepository.findByTheme(theme).stream().map(word -> {
            UserWordResponse w = new UserWordResponse();
            w.setId(word.getId());
            w.setTerm(word.getTerm());
            w.setTranslation(word.getTranslation());
            w.setTranscription(word.getTranscription());
            return w;
        }).toList();

        response.setWords(words);

//        resultRepository.findTopByUserAndThemeOrderByResultTimeDesc(user, theme)
//                .ifPresent(r -> response.setBestResultTime((int)r.getResultTime()));

        return response;
    }

    public MessageResponse deleteTheme(ThemeIdRequest request) {
        logger.info(UserWordExercisesLogMessages.THEME_DELETION_START, request.getThemeId());

        WordTheme theme = themeRepository.findById(request.getThemeId())
                .orElseThrow(() -> {
                    logger.error(UserWordExercisesLogMessages.THEME_NOT_FOUND, request.getThemeId());
                    return new NotFoundException("Набор не найден");
                });
        // Удалим сначала слова, связанные с темой, чтобы не нарушить внешние ключи
        wordRepository.deleteByTheme(theme);

        // Затем удалим саму тему
        themeRepository.delete(theme);
        logger.info(UserWordExercisesLogMessages.THEME_DELETED, request.getThemeId());

        return new MessageResponse("Набор успешно удален");
    }

    public MessageResponse editTheme(UUID themeId, UUID userId, WordThemeRequest updatedRequest) {
        logger.info(UserWordExercisesLogMessages.THEME_EDIT_START, themeId);

        WordTheme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> {
                    logger.error(UserWordExercisesLogMessages.THEME_NOT_FOUND, themeId);
                    return new NotFoundException("Тема не найдена");
                });
        // Обновляем название
        theme.setTitle(updatedRequest.getTitle());
        themeRepository.save(theme);

        // Удаляем старые слова
        wordRepository.deleteByTheme(theme);

        // Сохраняем новые
        updatedRequest.getWords().stream()
                .map(wordData -> {
                    UserWord word = new UserWord();
                    word.setTheme(theme);
                    word.setTerm(wordData.getTerm());
                    word.setTranslation(wordData.getTranslation());
                    word.setTranscription(wordData.getTranscription());
                    return word;
                })
                .forEach(wordRepository::save);

        logger.info(UserWordExercisesLogMessages.THEME_UPDATED, themeId, updatedRequest.getWords().size());

        return new MessageResponse("Тема успешно обновлена");
    }

}
