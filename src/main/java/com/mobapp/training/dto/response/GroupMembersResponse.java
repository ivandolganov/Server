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
public class GroupMembersResponse {
    private UUID groupId;
    private String courseName;
    private String groupName;
    private String groupCode;
    private int membersCount;
    private List<StudentResponse> students;
}
