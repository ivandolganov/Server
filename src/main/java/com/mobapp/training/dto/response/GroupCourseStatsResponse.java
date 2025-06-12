package com.mobapp.training.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupCourseStatsResponse {
    private UUID groupId;
    private String courseTitle;
    private String groupTitle;
    private String groupCode;
    private int countMember;
    private List<ModuleStats> modules;

    @Data
    public static class ModuleStats {
        private String moduleName;
        private int passThreshold;
        private List<Integer> studentModuleProgress;
        private List<Integer> studentModuleLexProgress;
    }
}

