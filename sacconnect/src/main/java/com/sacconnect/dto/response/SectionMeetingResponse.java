package com.sacconnect.dto.response;

public class SectionMeetingResponse {
    private String dayOfWeek;
    private Integer startMin;
    private Integer endMin;

    public SectionMeetingResponse() {
    }

    public SectionMeetingResponse(String dayOfWeek, Integer startMin, Integer endMin) {
        this.dayOfWeek = dayOfWeek;
        this.startMin = startMin;
        this.endMin = endMin;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public Integer getStartMin() {
        return startMin;
    }

    public Integer getEndMin() {
        return endMin;
    }
}
