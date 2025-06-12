package com.mobapp.training.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserIdRequest {
    private UUID studentId;

    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
    }
}
