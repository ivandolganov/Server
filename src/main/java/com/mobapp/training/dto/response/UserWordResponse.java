package com.mobapp.training.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class UserWordResponse {
    private UUID id;
    private String term;
    private String translation;
    private String transcription;
}