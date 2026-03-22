package com.sacconnect.repository;

import com.sacconnect.model.Professor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Optional<Professor> findByName(String name);
}