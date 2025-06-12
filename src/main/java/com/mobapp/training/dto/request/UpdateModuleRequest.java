package com.mobapp.training.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateModuleRequest {
    private String moduleName;
    private String moduleDescription;
    private String endDate;
    private int passTresshold;
    private List<UpdateNumberExerciseParamsRequest> numberExerciseParams;
    private List<UpdateNamesFacesExerciseParamsRequest> namesFacesExerciseParams;
    private List<UpdateDateExerciseParamsRequest> dateExerciseParams;
    private List<UpdateModuleWorldSetRequest> moduleWorldSetParams;

}
