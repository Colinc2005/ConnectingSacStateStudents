package com.sacconnect.repository;

import com.sacconnect.model.Course;
import com.sacconnect.model.CourseProfessor;
import com.sacconnect.model.CourseProfessorId;
import com.sacconnect.model.Professor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CourseProfessorRepository extends JpaRepository<CourseProfessor, CourseProfessorId> {
    Optional<CourseProfessor> findByCourseAndProfessor(Course course, Professor professor);

    @Query("select distinct cp.professor from CourseProfessor cp")
    List<Professor> findDistinctProfessors();
}
