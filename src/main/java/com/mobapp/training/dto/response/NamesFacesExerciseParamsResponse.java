package com.mobapp.training.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NamesFacesExerciseParamsResponse {
    private UUID exerciseId;
    private String nameCount;
    private int displayTimeMs;
    private String nameFormat;
    private double lastResultPercent;
}
