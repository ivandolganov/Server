package com.mobapp.training.controllers;

import com.mobapp.training.dto.request.CreateFolderRequest;
import com.mobapp.training.dto.request.FolderIdRequest;
import com.mobapp.training.models.Folder;
import com.mobapp.training.service.FolderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.mobapp.training.dto.response.*;
import com.mobapp.training.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/folders")
@PreAuthorize("hasRole('ROLE_USER')")
@RequiredArgsConstructor
public class FolderController {
    private final FolderService folderService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FolderResponse>> getUserFolders(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<FolderResponse> folders = folderService.getUserFolders(userDetails.getId());
        return ResponseEntity.ok(folders);
    }

    @PostMapping("/create-folder")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> createFolder(
            @RequestBody CreateFolderRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(folderService.createFolder(userDetails.getId(), request));
    }

    @DeleteMapping("/delete-folder")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> deleteFolder(
            @RequestBody FolderIdRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(folderService.deleteFolder(userDetails.getId(), request.getFolderId()));
    }

    @GetMapping("/{folderId}/themes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ThemeInFolderResponse>> getThemesInFolder(
            @PathVariable UUID folderId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ThemeInFolderResponse> themes = folderService.getThemesInFolder(
                userDetails.getId(),
                folderId
        );
        return ResponseEntity.ok(themes);
    }
}