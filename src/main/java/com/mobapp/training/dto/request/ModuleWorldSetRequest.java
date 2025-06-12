package com.mobapp.training.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ModuleWorldSetRequest {
    private UUID themeId;
    private int questionCount;
    private boolean instantAnswer;
    private String answerType;
}
