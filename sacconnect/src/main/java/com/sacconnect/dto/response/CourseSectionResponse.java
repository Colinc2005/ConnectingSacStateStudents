package com.sacconnect.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class CourseSectionResponse {
    private Long sectionId;
    private String sectionNumber;
    private String sourceCrn;
    private String location;
    private String modality;
    private String term;
    private Long professorId;
    private String professorName;
    private BigDecimal professorOverallRating;
    private Integer professorReviewCount;
    private List<SectionMeetingResponse> meetings;

    public CourseSectionResponse() {
    }

    public CourseSectionResponse(
            Long sectionId,
            String sectionNumber,
            String sourceCrn,
            String location,
            String modality,
            String term,
            Long professorId,
            String professorName,
            BigDecimal professorOverallRating,
            Integer professorReviewCount,
            List<SectionMeetingResponse> meetings
    ) {
        this.sectionId = sectionId;
        this.sectionNumber = sectionNumber;
        this.sourceCrn = sourceCrn;
        this.location = location;
        this.modality = modality;
        this.term = term;
        this.professorId = professorId;
        this.professorName = professorName;
        this.professorOverallRating = professorOverallRating;
        this.professorReviewCount = professorReviewCount;
        this.meetings = meetings;
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

    public String getLocation() {
        return location;
    }

    public String getModality() {
        return modality;
    }

    public String getTerm() {
        return term;
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
