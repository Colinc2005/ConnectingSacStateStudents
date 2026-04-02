package com.sacconnect.model;

import java.io.Serializable;
import java.util.Objects;

public class CourseProfessorId implements Serializable {

    private Long course;
    private Long professor;

    public CourseProfessorId() {
    }

    public CourseProfessorId(Long course, Long professor) {
        this.course = course;
        this.professor = professor;
    }

    public Long getCourse() {
        return course;
    }

    public void setCourse(Long course) {
        this.course = course;
    }

    public Long getProfessor() {
        return professor;
    }

    public void setProfessor(Long professor) {
        this.professor = professor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseProfessorId that)) {
            return false;
        }
        return Objects.equals(course, that.course)
                && Objects.equals(professor, that.professor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(course, professor);
    }
}