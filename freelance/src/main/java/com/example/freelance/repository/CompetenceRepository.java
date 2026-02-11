package com.example.freelance.repository;

import com.example.freelance.model.Competence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Assala Hamoudi
 */
@Repository
public interface CompetenceRepository extends JpaRepository<Competence, Long> {
}
