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
public class UpdateNamesFacesExerciseParamsRequest {
    private UUID exerciseId;
    private int nameCount;
    private int displayTimeMs;
    private String nameFormat;
}
