package com.mobapp.training.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "date_exercise_params")
public class DateExerciseParams {
    @Id
    private UUID id;

    @OneToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    private int dateCount;
    private LocalDate dateRangeStart;
    private LocalDate dateRangeEnd;
    private int displayTimeMs;

    @Enumerated(EnumType.STRING)
    private DateFormat format; // Используем enum с форматами

    private String formatDate(LocalDate date) {
        if (date == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                format.getPattern(),
                new Locale("ru")
        );
        return date.format(formatter);
    }

    @Getter
    @RequiredArgsConstructor
    public enum DateFormat {
        TEXT_FORMAT("дд месяц гггг", "dd MMMM yyyy"),
        NUMERIC_FORMAT("дд.мм.гггг", "dd.MM.yyyy");

        private final String displayName;
        private final String pattern;

        public static DateFormat fromDisplayName(String displayName) {
            return Arrays.stream(values())
                    .filter(format -> format.getDisplayName().equals(displayName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown display name: " + displayName));
        }


    }
    /**
     * Возвращает дату начала в текстовом формате
     */
    public String getFormattedStartDate() {
        return formatDate(dateRangeStart);
    }

    /**
     * Возвращает дату окончания в текстовом формате
     */
    public String getFormattedEndDate() {
        return formatDate(dateRangeEnd);
    }

    /**
     * Парсит строку с датой в LocalDate согласно выбранному формату
     */
    public LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.isBlank()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format.getPattern(), new Locale("ru"));
        return LocalDate.parse(dateString, formatter);
    }

    /**
     * Проверяет, соответствует ли строка с датой текущему формату
     */
    public boolean isValidDate(String dateString) {
        try {
            parseDate(dateString);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }
}

