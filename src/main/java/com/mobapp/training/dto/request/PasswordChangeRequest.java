package com.mobapp.training.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PasswordChangeRequest {
    private String newPassword;
}
