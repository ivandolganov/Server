package com.mobapp.training.dto.response;

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
public class CoursesTeacherResponse {
    private UUID courseId;
    private String courseTitle;
    private int modulesCount;
    private List<String> groups;
}

