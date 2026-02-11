package com.example.freelance.repository;

import com.example.freelance.model.MissionFreelance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MissionFreelanceRepository extends JpaRepository<MissionFreelance, Long> {

    List<MissionFreelance> findByFreelancerId(Long freelancerId);

    @Query("SELECT mf FROM MissionFreelance mf WHERE mf.dateFin = :targetDate AND (mf.notificationEnvoyee = false OR mf.notificationEnvoyee IS NULL)")
    List<MissionFreelance> findMissionsEndingOn(@Param("targetDate") LocalDate targetDate);
}
