package com.mobapp.training.dto.request;

import lombok.Getter;

@Getter
public class SettingsRequest {
    private String firstName;
    private String lastName;
    private String patronymic;
    private int yearOfBirth;
    private String gender;
}
