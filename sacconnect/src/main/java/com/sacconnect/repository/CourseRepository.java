package com.sacconnect.repository;

import com.sacconnect.model.Course;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCode(String code);
    List<Course> findByMajor_NameIgnoreCaseOrderByCodeAsc(String majorName);
    List<Course> findByCodeStartingWithIgnoreCaseOrderByCodeAsc(String codePrefix);
}
