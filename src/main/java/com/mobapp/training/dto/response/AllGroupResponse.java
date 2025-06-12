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
public class AllGroupResponse {
    private UUID groupId;
    private String groupName;
    private String courseName;
    private String groupCode;
    private int groupMembersCount;
}
