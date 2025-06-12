package com.mobapp.training.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NameFaceRequest {
    private int nameCount;
    private String format;

    public void setFormat(String format) {
        switch (format) {
            case "1" -> this.format = "fullName";
            case "2" -> this.format = "name";
            case "3" -> this.format = "patronymic";
            case "4" -> this.format = "lastName";
            default -> this.format = "name";
        }
    }
}
