package com.mobapp.training.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class WordThemeRequest {
    private String title;
    private List<WordItemsDto> words;
}
