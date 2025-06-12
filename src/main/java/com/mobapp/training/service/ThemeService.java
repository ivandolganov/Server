package com.mobapp.training.service;

import com.mobapp.training.dto.request.*;
import com.mobapp.training.dto.response.*;
import com.mobapp.training.exception.*;
import com.mobapp.training.logging.ThemeLogMessages;
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
@RequiredArgsConstructor
@Transactional
public class ThemeService {

    private static final Logger logger = LoggerFactory.getLogger(ThemeService.class);
    private final ThemeRepository themeRepository;
    private final WordItemsRepository wordItemsRepository;
    private final UsersRepository usersRepository;

    public MessageResponse createThemeWithWords(WordThemeRequest request) {
        // Создаем тему
        Theme theme = new Theme();
        theme.setName(request.getTitle());
        Theme savedTheme = themeRepository.save(theme);
        logger.info(ThemeLogMessages.THEME_CREATED, savedTheme.getId(), savedTheme.getName());
        // Создаем слова
        List<WordItems> savedWords = request.getWords().stream()
                .map(wordDto -> createWordItem(wordDto, theme))
                .collect(Collectors.toList());

        wordItemsRepository.saveAll(savedWords);
        logger.info(ThemeLogMessages.WORDS_ADDED, savedTheme.getId(), savedWords.size());

        return new MessageResponse("Тема успешна создана!");
    }

    private WordItems createWordItem(WordItemsDto dto, Theme theme) {
        WordItems word = new WordItems();
        word.setWord(dto.getTerm());
        word.setTranslation(dto.getTranslation());
        word.setTranscription(dto.getTranscription());
        word.setTheme(theme);
        return word;
    }

    public ThemesResponse getThemesWithWords(UUID userId) {
        Users creator = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));

        // Получаем все темы, созданные этим пользователем
        List<Theme> userThemes = themeRepository.findByCreatedBy(creator);

        // Преобразуем в DTO
        ThemesResponse response = new ThemesResponse();
        response.setThemes(
                userThemes.stream()
                        .map(theme -> {
                            ThemesResponse.Theme themeDto = new ThemesResponse.Theme();
                            themeDto.setThemeId(theme.getId());
                            themeDto.setThemeName(theme.getName());
                            return themeDto;
                        })
                        .collect(Collectors.toList())
        );

        return response;
    }

    public MessageResponse editThemeWithWords(EditWordThemeRequest request) {
        logger.info(ThemeLogMessages.THEME_EDIT_START, request.getThemeId());

        Theme theme = themeRepository.findById(request.getThemeId())
                .orElseThrow(() -> {
                    logger.error(ThemeLogMessages.THEME_NOT_FOUND, request.getThemeId());
                    return new NotFoundException("Набор не найден");
                });

        theme.setName(request.getTitle());
        themeRepository.save(theme);

        wordItemsRepository.deleteAllByThemeId(request.getThemeId());

        // Создаем слова
        List<WordItems> savedWords = request.getWords().stream()
                .map(wordDto -> createWordItem(wordDto, theme))
                .collect(Collectors.toList());

        wordItemsRepository.saveAll(savedWords);
        logger.info(ThemeLogMessages.NEW_WORDS_ADDED, request.getThemeId(), savedWords.size());
        logger.info(ThemeLogMessages.THEME_UPDATED, request.getThemeId());


        return new MessageResponse("Тема успешна изменена!");
    }

    public MessageResponse deleteThemeWithWords(ThemeIdRequest request) {
        logger.info(ThemeLogMessages.THEME_DELETION_START, request.getThemeId());

        Theme theme = themeRepository.findById(request.getThemeId())
                .orElseThrow(() -> {
                    logger.error(ThemeLogMessages.THEME_NOT_FOUND, request.getThemeId());
                    return new NotFoundException("Набор не найден");
                });

        // 1. Удаляем все слова темы
        wordItemsRepository.deleteAllByThemeId(request.getThemeId());
        logger.info(ThemeLogMessages.WORDS_REMOVED, request.getThemeId());

        // 2. Удаляем саму тему
        themeRepository.deleteById(request.getThemeId());
        logger.info(ThemeLogMessages.THEME_DELETED, request.getThemeId());

        return new MessageResponse("Тема успешна удалена!");
    }

}