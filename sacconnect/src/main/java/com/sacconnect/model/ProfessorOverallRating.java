package com.sacconnect.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "professor_overall_ratings")
public class ProfessorOverallRating {

    @Id
    @Column(name = "professor_id", nullable = false)
    private Long professorId;

    @Column(name = "overall_rating", nullable = false, precision = 2, scale = 1)
    private BigDecimal overallRating;

    @Column(name = "review_count", nullable = false)
    private Integer reviewCount;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public ProfessorOverallRating() {
    }

    public ProfessorOverallRating(Long professorId, BigDecimal overallRating, Integer reviewCount) {
        this.professorId = professorId;
        this.overallRating = overallRating;
        this.reviewCount = reviewCount;
    }

    public Long getProfessorId() {
        return professorId;
    }

    public void setProfessorId(Long professorId) {
        this.professorId = professorId;
    }

    public BigDecimal getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(BigDecimal overallRating) {
        this.overallRating = overallRating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
