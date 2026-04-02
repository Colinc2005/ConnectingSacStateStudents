package com.sacconnect.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sacconnect.model.CourseSection;

public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {
    Optional<CourseSection> findBySourceCrn(String sourceCrn);
    List<CourseSection> findByCourseIdOrderBySectionNumberAsc(Long courseId);
}
