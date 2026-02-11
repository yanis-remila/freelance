package com.example.freelance.repository;
import com.example.freelance.model.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {

}