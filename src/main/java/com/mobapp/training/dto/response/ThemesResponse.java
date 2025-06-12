package com.mobapp.training.dto.response;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ThemesResponse {
    private List<Theme> themes;

    @Data
    public static class Theme {
        private UUID themeId;
        private String themeName;
    }
}
