package com.mobapp.training.service;

import com.mobapp.training.dto.request.CreateFolderRequest;
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
public class FolderService {
    private final FolderRepository folderRepository;
    private final FolderTopicRepository folderTopicRepository;
    private final WordThemeRepository topicRepository;
    private final UsersRepository usersRepository;
    private final WordThemeRepository wordThemeRepository;
    private final WordThemeResultRepository resultRepository;

    @Transactional
    public MessageResponse createFolder(UUID userId, CreateFolderRequest request) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> {
                    return new NotFoundException("Студент не найден");
                });

        Folder folder = new Folder();
        folder.setName(request.getFolderName());
        folder.setUser(user);

        Folder savedFolder = folderRepository.save(folder);

        if (!request.getThemesId().isEmpty()) {
            List<FolderTopic> folderTopics = request.getThemesId().stream()
                    .map(topicId -> {
                        FolderTopic ft = new FolderTopic();
                        ft.setFolder(savedFolder);
                        WordTheme theme = wordThemeRepository.findById(topicId)
                                .orElseThrow(() -> {
                                    return new NotFoundException("Набор не найден!");
                                });
                        ft.setTheme(theme); // Просто создаем Topic с ID
                        return ft;
                    })
                    .collect(Collectors.toList());

            folderTopicRepository.saveAll(folderTopics);
        }


        folderRepository.save(folder);

        return new MessageResponse("Папка успешно создана!");
    }

    @Transactional
    public List<FolderResponse> getUserFolders(UUID userId) {
        List<Folder> folders = folderRepository.findByUserId(userId);

        return folders.stream()
                .map(folder -> {
                    int topicsCount = folderTopicRepository.countByFolderId(folder.getId());
                    return new FolderResponse(
                            folder.getId(),
                            folder.getName(),
                            topicsCount
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageResponse deleteFolder(UUID userId, UUID folderId) {
        folderRepository.deleteByIdAndUserId(folderId, userId);

        return new MessageResponse("Папка успешно удалена!");
    }


    @Transactional
    public List<ThemeInFolderResponse> getThemesInFolder(UUID userId, UUID folderId) {
        // 1. Проверяем существование пользователя
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        // 2. Проверяем принадлежность папки пользователю
        if (!folderRepository.existsByIdAndUserId(folderId, userId)) {
            throw new NotFoundException("Папка не найдена или нет доступа");
        }

        // 3. Получаем темы с использованием исправленного метода репозитория
        List<FolderTopic> folderTopics = folderTopicRepository.findByFolderId(folderId);

        // 4. Преобразуем в DTO
        return folderTopics.stream()
                .map(ft -> {
                    WordTheme theme = ft.getTheme();
                    ThemeInFolderResponse response = new ThemeInFolderResponse();
                    response.setId(theme.getId());
                    response.setName(theme.getTitle());
                    response.setWordCount(theme.getWords().size());

                    // 5. Добавляем информацию о последней попытке (если есть)
                    resultRepository.findTopByUserAndThemeOrderByAttemptedAtDesc(user, theme)
                            .ifPresent(result -> {
                                response.setLastResult((int) result.getResultTime());
                            });

                    return response;
                })
                .collect(Collectors.toList());
    }

}