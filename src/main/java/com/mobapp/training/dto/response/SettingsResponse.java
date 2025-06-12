package com.mobapp.training.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SettingsResponse {
    private String firstName;
    private String lastName;
    private String patronymic;
    private String email;
    private Integer yearOfBirth;
    private String gender;
}
