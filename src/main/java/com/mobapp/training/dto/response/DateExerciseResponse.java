package com.mobapp.training.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DateExerciseResponse {
    private UUID exerciseId;
    private List<LocalDate> dates;
    private int datesCount;
    private int displayTimeMs;
}

