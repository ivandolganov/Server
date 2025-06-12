package com.mobapp.training.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ModulesResponse {
    private UUID moduleId;
    private String moduleTitle;
    private String description;
    private LocalDate endDate;
    private int averageResult;
    private int passThreshold;


}