package com.mobapp.training.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentCourseStatsResponse {
    private UUID courseId;
    private String userFulName;
    private String courseTitle;
    private int courseAverage;

    private List<ModuleStats> modules;

    @Data
    public static class ModuleStats {
        private UUID moduleId;
        private String moduleName;
        private int moduleAverage;
        private List<ExerciseStats> exercises;
    }

    @Data
    public static class ExerciseStats {
        private UUID exerciseId;
        private String exerciseType;
        private int lastAttemptPercent;
    }
}

