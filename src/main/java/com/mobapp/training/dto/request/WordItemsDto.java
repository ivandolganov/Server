package com.mobapp.training.dto.request;

import lombok.Data;

@Data
public class WordItemsDto {
    private String term;
    private String translation;
    private String transcription;
}