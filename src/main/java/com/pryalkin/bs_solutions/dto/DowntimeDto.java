package com.pryalkin.bs_solutions.dto;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class DowntimeDto {
    private Long id;
    private Long forkliftId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private String downtimeDuration; // Формат "X ч. Y мин."

    // Метод расчета
    public void calculateDuration() {
        LocalDateTime end = (endDate != null) ? endDate : LocalDateTime.now();
        long minutes = Duration.between(startDate, end).toMinutes();
        long hours = minutes / 60;
        long mins = minutes % 60;
        this.downtimeDuration = hours + " ч. " + mins + " мин.";
    }
}
