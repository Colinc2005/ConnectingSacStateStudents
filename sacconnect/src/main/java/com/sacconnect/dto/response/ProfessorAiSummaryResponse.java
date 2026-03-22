package com.sacconnect.dto.response;

import java.util.List;

public class ProfessorAiSummaryResponse {
    private Long professorId;
    private String professorName;
    private String threeSentenceSummary;
    private List<String> pros;
    private List<String> cons;
    private Integer reviewsAnalyzed;
    private String source;

    public ProfessorAiSummaryResponse(
            Long professorId,
            String professorName,
            String threeSentenceSummary,
            List<String> pros,
            List<String> cons,
            Integer reviewsAnalyzed,
            String source
    ) {
        this.professorId = professorId;
        this.professorName = professorName;
        this.threeSentenceSummary = threeSentenceSummary;
        this.pros = pros;
        this.cons = cons;
        this.reviewsAnalyzed = reviewsAnalyzed;
        this.source = source;
    }

    public Long getProfessorId() {
        return professorId;
    }

    public String getProfessorName() {
        return professorName;
    }

    public String getThreeSentenceSummary() {
        return threeSentenceSummary;
    }

    public List<String> getPros() {
        return pros;
    }

    public List<String> getCons() {
        return cons;
    }

    public Integer getReviewsAnalyzed() {
        return reviewsAnalyzed;
    }

    public String getSource() {
        return source;
    }
}
