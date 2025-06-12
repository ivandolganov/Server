package com.mobapp.training.dto.response;

import com.mobapp.training.models.StudentGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Setter
@NoArgsConstructor
@Getter
public class EditCourseResponse {
    private UUID courseId;
    private String courseName;
    private List<GroupResponse> groups;
}
