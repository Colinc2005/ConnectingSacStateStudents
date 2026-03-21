package com.sacconnect.repository;

import com.sacconnect.model.Course;
import com.sacconnect.model.CourseProfessor;
import com.sacconnect.model.Professor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseProfessorRepository extends JpaRepository<CourseProfessor, Long> {
    Optional<CourseProfessor> findByCourseAndProfessor(Course course, Professor professor);
}