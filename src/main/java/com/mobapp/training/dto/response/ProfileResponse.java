package com.mobapp.training.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    String fullName;
    String email;
    Integer yearOfBirth;
    String gender;
    String firstName;
    String lastName;
    String patronymic;
}
