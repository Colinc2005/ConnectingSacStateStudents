package com.sacconnect.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class ScheduleSectionResponse {
    private Long courseId;
    private String courseCode;
    private String courseTitle;
    private Long sectionId;
    private String sectionNumber;
    private String sourceCrn;
    private Long professorId;
    private String professorName;
    private BigDecimal professorOverallRating;
    private Integer professorReviewCount;
    private List<SectionMeetingResponse> meetings;

    public ScheduleSectionResponse(
            Long courseId,
            String courseCode,
            String courseTitle,
            Long sectionId,
            String sectionNumber,
            String sourceCrn,
            Long professorId,
            String professorName,
            BigDecimal professorOverallRating,
            Integer professorReviewCount,
            List<SectionMeetingResponse> meetings
    ) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.sectionId = sectionId;
        this.sectionNumber = sectionNumber;
        this.sourceCrn = sourceCrn;
        this.professorId = professorId;
        this.professorName = professorName;
        this.professorOverallRating = professorOverallRating;
        this.professorReviewCount = professorReviewCount;
        this.meetings = meetings;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public String getSectionNumber() {
        return sectionNumber;
    }

    public String getSourceCrn() {
        return sourceCrn;
    }

    public Long getProfessorId() {
        return professorId;
    }

    public String getProfessorName() {
        return professorName;
    }

    public BigDecimal getProfessorOverallRating() {
        return professorOverallRating;
    }

    public Integer getProfessorReviewCount() {
        return professorReviewCount;
    }

    public List<SectionMeetingResponse> getMeetings() {
        return meetings;
    }
}
