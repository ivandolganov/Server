package com.mobapp.training.repo;

import com.mobapp.training.models.FolderTopic;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FolderTopicRepository extends JpaRepository<FolderTopic, UUID> {
    boolean existsByFolderIdAndThemeId(UUID folderId, UUID topicId);
    
    @Modifying
    @Query("DELETE FROM FolderTopic ft WHERE ft.folder.id = :folderId AND ft.theme.id = :topicId")
    void deleteByFolderIdAndTopicId(@Param("folderId") UUID folderId,
                                  @Param("topicId") UUID topicId);

    int countByFolderId(UUID folderId);

    // Вариант 1: Простой метод с @EntityGraph
    @EntityGraph(attributePaths = {"theme"})
    List<FolderTopic> findByFolderId(UUID folderId);

}