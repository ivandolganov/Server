package com.mobapp.training.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegistrationRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Integer birthYear;
    private String gender;
    private String groupCode; // только для студентов
    private String role;
}