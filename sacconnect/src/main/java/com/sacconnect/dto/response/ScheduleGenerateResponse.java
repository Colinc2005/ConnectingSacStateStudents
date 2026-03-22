package com.sacconnect.dto.response;

import java.util.List;

public class ScheduleGenerateResponse {
    private int totalValidSchedules;
    private boolean truncated;
    private List<ScheduleOptionResponse> overallBest;
    private List<ScheduleOptionResponse> leastDays;
    private List<ScheduleOptionResponse> bestTime;
    private List<ScheduleOptionResponse> smallestGaps;

    public ScheduleGenerateResponse(
            int totalValidSchedules,
            boolean truncated,
            List<ScheduleOptionResponse> overallBest,
            List<ScheduleOptionResponse> leastDays,
            List<ScheduleOptionResponse> bestTime,
            List<ScheduleOptionResponse> smallestGaps
    ) {
        this.totalValidSchedules = totalValidSchedules;
        this.truncated = truncated;
        this.overallBest = overallBest;
        this.leastDays = leastDays;
        this.bestTime = bestTime;
        this.smallestGaps = smallestGaps;
    }

    public int getTotalValidSchedules() {
        return totalValidSchedules;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public List<ScheduleOptionResponse> getOverallBest() {
        return overallBest;
    }

    public List<ScheduleOptionResponse> getLeastDays() {
        return leastDays;
    }

    public List<ScheduleOptionResponse> getBestTime() {
        return bestTime;
    }

    public List<ScheduleOptionResponse> getSmallestGaps() {
        return smallestGaps;
    }
}
