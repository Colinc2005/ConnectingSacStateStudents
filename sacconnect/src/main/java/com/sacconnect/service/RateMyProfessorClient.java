package com.sacconnect.service;

import java.util.List;
import java.util.Optional;

public interface RateMyProfessorClient {

    Optional<String> findSchoolId(String schoolName) throws Exception;

    List<RateMyProfessorTeacher> searchTeachers(String teacherName, String schoolId) throws Exception;

    record RateMyProfessorTeacher(
            String id,
            String firstName,
            String lastName,
            String schoolName,
            Double avgRating,
            Integer numRatings
    ) {
        public String fullName() {
            return (firstName + " " + lastName).trim();
        }
    }
}
