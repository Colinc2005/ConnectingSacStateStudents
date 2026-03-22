package com.sacconnect.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "section_meetings")
public class SectionMeeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    private CourseSection section;

    @Column(name = "day_of_week", nullable = false, length = 10)
    private String dayOfWeek;

    @Column(name = "start_min", nullable = false)
    private Integer startMin;

    @Column(name = "end_min", nullable = false)
    private Integer endMin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CourseSection getSection() {
        return section;
    }

    public void setSection(CourseSection section) {
        this.section = section;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getStartMin() {
        return startMin;
    }

    public void setStartMin(Integer startMin) {
        this.startMin = startMin;
    }

    public Integer getEndMin() {
        return endMin;
    }

    public void setEndMin(Integer endMin) {
        this.endMin = endMin;
    }
}
