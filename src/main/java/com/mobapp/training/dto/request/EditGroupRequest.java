package com.mobapp.training.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class EditGroupRequest {
    private UUID groupId;
    private String groupName;
}
