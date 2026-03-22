package com.sacconnect.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class ScheduleOptionResponse {
    private double score;
    private int distinctDays;
    private int totalGapMinutes;
    private int outsidePreferredMinutes;
    private BigDecimal totalProfessorRating;
    private List<ScheduleSectionResponse> sections;

    public ScheduleOptionResponse(
            double score,
            int distinctDays,
            int totalGapMinutes,
            int outsidePreferredMinutes,
            BigDecimal totalProfessorRating,
            List<ScheduleSectionResponse> sections
    ) {
        this.score = score;
        this.distinctDays = distinctDays;
        this.totalGapMinutes = totalGapMinutes;
        this.outsidePreferredMinutes = outsidePreferredMinutes;
        this.totalProfessorRating = totalProfessorRating;
        this.sections = sections;
    }

    public double getScore() {
        return score;
    }

    public int getDistinctDays() {
        return distinctDays;
    }

    public int getTotalGapMinutes() {
        return totalGapMinutes;
    }

    public int getOutsidePreferredMinutes() {
        return outsidePreferredMinutes;
    }

    public BigDecimal getTotalProfessorRating() {
        return totalProfessorRating;
    }

    public List<ScheduleSectionResponse> getSections() {
        return sections;
    }
}
