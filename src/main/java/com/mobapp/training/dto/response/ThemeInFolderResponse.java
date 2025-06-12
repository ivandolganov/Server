package com.mobapp.training.dto.response;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeInFolderResponse {
    private UUID id;
    private String name;
    private int wordCount;
    private int lastResult;
}