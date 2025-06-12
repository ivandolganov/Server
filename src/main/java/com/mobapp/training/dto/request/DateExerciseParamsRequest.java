package com.mobapp.training.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DateExerciseParamsRequest {
    private int dateCount;
    private String dateRangeStart;
    private String dateRangeEnd;
    private int displayTimeMs;
    private String format;
}
