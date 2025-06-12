package com.mobapp.training.repo;

import com.mobapp.training.models.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FolderRepository extends JpaRepository<Folder, UUID> {
    List<Folder> findByUserId(UUID userId);
    
    boolean existsByIdAndUserId(UUID folderId, UUID userId);
    
    @Modifying
    @Query("DELETE FROM Folder f WHERE f.id = :folderId AND f.user.id = :userId")
    void deleteByIdAndUserId(@Param("folderId") UUID folderId,
                           @Param("userId") UUID userId);

}