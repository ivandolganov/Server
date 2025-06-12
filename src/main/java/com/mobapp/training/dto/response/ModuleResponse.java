package com.mobapp.training.dto.response;

import com.mobapp.training.dto.request.DateExerciseParamsRequest;
import com.mobapp.training.dto.request.ModuleWorldSetRequest;
import com.mobapp.training.dto.request.NamesFacesExerciseParamsRequest;
import com.mobapp.training.dto.request.NumberExerciseParamsRequest;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModuleResponse {
    private UUID moduleId;
    private String moduleTitle;
    private String description;
    private LocalDate endDate;
    private Integer passThreshold;
    private String teacherName;
    private int avgResult;
    private List<NumberExerciseParamsResponse> numberExerciseParams;
    private List<NamesFacesExerciseParamsResponse> namesFacesExerciseParams;
    private List<DateExerciseParamsResponse> dateExerciseParams;
    private List<ModuleWorldSetResponse> moduleWorldSetParams;

}
