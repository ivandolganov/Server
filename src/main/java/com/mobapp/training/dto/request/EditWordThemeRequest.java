package com.mobapp.training.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class EditWordThemeRequest {
    private UUID themeId;
    private String title;
    private List<WordItemsDto> words;
}