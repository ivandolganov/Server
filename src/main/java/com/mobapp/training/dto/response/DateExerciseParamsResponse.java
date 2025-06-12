package com.mobapp.training.dto.response;

import com.mobapp.training.models.DateExerciseParams;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DateExerciseParamsResponse {
    private UUID exerciseId;
    private int dateCount;
    private LocalDate dateRangeStart;
    private LocalDate dateRangeEnd;
    private int displayTimeMs;
    private String format;
    private int lastResultPercent;
}
