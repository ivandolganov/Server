package com.mobapp.training.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class WordThemeResponse {
    private UUID themeId;
    private String title;
    private int lastResultTime;
}