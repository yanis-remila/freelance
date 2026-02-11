package com.example.freelance.controller;

import com.example.freelance.dto.FreelancerRecommendationDTO;
import com.example.freelance.model.Mission;
import com.example.freelance.repository.MissionRepository;
import com.example.freelance.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Assala Hamoudi
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommandations")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final MissionRepository missionRepository;

    @GetMapping("/mission/{missionId}")
    public List<FreelancerRecommendationDTO> getRecommendations(@PathVariable Long missionId) {
        log.info("Récupérer les freelances recommendés pour la mission {}", missionId);
        return recommendationService.recommendFreelancersForMission(missionId);
    }

    @GetMapping("/freelance/{freelanceId}")
    public List<Mission> getMissionRecommendations(@PathVariable Long freelanceId) {
        log.info("Récupérer les missions du freelancer {}", freelanceId);
        return missionRepository.findByFreelancerId(freelanceId);
    }
}
