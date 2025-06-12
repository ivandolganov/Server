package com.mobapp.training.dto.request;

import com.mobapp.training.dto.response.StudentCourseStatsResponse;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class UserWordRequest {
    private List<Words> words;

    @Data
    public static class Words {
        private String term;
        private String translation;
        private String transcription;
    }
}