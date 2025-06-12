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
public class NamesFacesExerciseParamsRequest {
    private String nameCount;
    private int displayTimeMs;
    private String nameFormat;
}
