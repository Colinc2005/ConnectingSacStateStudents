package com.sacconnect.dto.request;

import java.util.List;

public class ScheduleGenerateRequest {
    private List<Long> courseIds;
    private Integer preferredStartMin;
    private Integer preferredEndMin;
    private Integer topNPerCategory;

    public List<Long> getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(List<Long> courseIds) {
        this.courseIds = courseIds;
    }

    public Integer getPreferredStartMin() {
        return preferredStartMin;
    }

    public void setPreferredStartMin(Integer preferredStartMin) {
        this.preferredStartMin = preferredStartMin;
    }

    public Integer getPreferredEndMin() {
        return preferredEndMin;
    }

    public void setPreferredEndMin(Integer preferredEndMin) {
        this.preferredEndMin = preferredEndMin;
    }

    public Integer getTopNPerCategory() {
        return topNPerCategory;
    }

    public void setTopNPerCategory(Integer topNPerCategory) {
        this.topNPerCategory = topNPerCategory;
    }
}
