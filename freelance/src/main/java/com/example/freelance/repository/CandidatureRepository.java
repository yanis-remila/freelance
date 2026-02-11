package com.example.freelance.repository;

import com.example.freelance.model.Candidature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Assala Hamoudi
 */
@Repository
public interface CandidatureRepository extends JpaRepository<Candidature, Long> {

}
