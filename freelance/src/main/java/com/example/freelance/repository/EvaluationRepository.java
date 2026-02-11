package com.example.freelance.repository;

import com.example.freelance.model.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    @Query("""
        SELECT e
        FROM Evaluation e
        WHERE e.mission.id IN :missionIds
    """)
    List<Evaluation> findAllByMissionIds(@Param("missionIds") Collection<Long> missionIds);

    List<Evaluation> findAllByNoteLessThanEqual(Double note);

    List<Evaluation> findAllByNoteGreaterThan(Double note);
}
