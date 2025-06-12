package com.mobapp.training.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class DeleteGroupRequest {
    private UUID groupId;
}
