package com.mobapp.training.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ModuleWorldSetResponse {
    private UUID exerciseId;
    private UUID themeId;
    private int questionCount;
    private boolean instantAnswer;
    private String answerType;
    private int lastResultPercent;
}

