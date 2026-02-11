package com.example.freelance.repository;

import com.example.freelance.model.MissionCompetence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionCompetenceRepository extends JpaRepository<MissionCompetence, Long> {
    @Query("SELECT mc.competence.id FROM MissionCompetence mc WHERE mc.mission.id = :missionId")
    List<Long> findCompetenceIdsByMissionId(@Param("missionId") Long missionId);
}
