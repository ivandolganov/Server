package com.mobapp.training.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateModuleRequest {
    private String moduleName;
    private String moduleDescription;
    private String endDate;
    private int passTresshold;
    private List<NumberExerciseParamsRequest> numberExerciseParams;
    private List<NamesFacesExerciseParamsRequest> namesFacesExerciseParams;
    private List<DateExerciseParamsRequest> dateExerciseParams;
    private List<ModuleWorldSetRequest> moduleWorldSetParams;

}
