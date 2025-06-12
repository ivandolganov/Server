package com.mobapp.training.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Schema(description = "DTO для создания нового курса")
@AllArgsConstructor
public class CreateGroupRequest {
    private String groupName;
}
