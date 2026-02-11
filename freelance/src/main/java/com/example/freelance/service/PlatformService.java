package com.example.freelance.service;
import com.example.freelance.model.Freelancer;
import com.example.freelance.model.Mission;
import com.example.freelance.model.Platform;
import com.example.freelance.model.enums.MissionStatut;
import com.example.freelance.repository.MissionRepository;
import com.example.freelance.repository.PlatformRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import java.util.List;
import java.util.Optional;


@Service
public class PlatformService {

    @Autowired
    private PlatformRepository platformRepository;

    @Autowired
    private MissionRepository missionRepository;

    public Platform createPlatform() {
        Platform platform = new Platform();
        return platformRepository.save(platform);
    }

    public Optional<Platform> getPlatformById(Long id) {
        return platformRepository.findById(id);
    }



    public boolean isEligible(Freelancer freelancer, Mission mission) {

        if (!mission.getStatut().equals(MissionStatut.EN_ATTENTE)) {
            return false;
        }
        if (freelancer.getExperience() < 1) {
            return false;
        }
        if (mission.getBudget() < 500) {
            return false;
        }
        if (!freelancer.getCompetences().containsAll(mission.getCompetences())) {
            return false;
        }
        return true;
    }



}