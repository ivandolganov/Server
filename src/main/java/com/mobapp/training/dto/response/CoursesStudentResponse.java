package com.mobapp.training.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CoursesStudentResponse {
    private UUID courseId;
    private String courseTitle;
    private int modulesCount;
    private String teacherName;
    private int averageResult;
}
