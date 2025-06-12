package com.mobapp.training.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNumberExerciseParamsRequest {
    private UUID exerciseId;
    private int numberCount;
    private int base;
    private int digit_lenth;
    private int minValue;
    private int maxValue;
    private int displayTimeMs;
}
