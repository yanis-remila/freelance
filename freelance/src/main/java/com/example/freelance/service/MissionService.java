package com.example.freelance.service;

import com.example.freelance.dto.MissionEndingEvent;
import com.example.freelance.model.*;
import com.example.freelance.model.enums.MissionStatut;
import com.example.freelance.repository.MissionRepository;
import com.example.freelance.repository.ClientRepository;
import com.example.freelance.repository.CompetenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final ClientRepository clientRepository;
    private final CompetenceRepository competenceRepository;

    @Autowired
    private MissionEventProducer missionEventProducer;

    @Autowired
    private FreelanceService freelanceService;


    public Mission saveMission(Mission m) {
        Mission mission = missionRepository.save(m);
        List<Freelancer> availableFreelancers = freelanceService.getAllAvailableFreelancers();

        availableFreelancers.forEach(freelancer -> {
            MissionEndingEvent event = new MissionEndingEvent(
                    mission.getId(),
                    freelancer.getId(),
                    freelancer.getEmail(),
                    mission.getTitre(),
                    null,
                    0
            );
            missionEventProducer.publishMissionAssignedEvent(event);
        });

        return mission;
    }
    public Mission updateMissionStatus(Long missionId, MissionStatut newStatut) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission non trouvée"));


        if (!isValidStatusTransition(mission.getStatut(), newStatut)) {
            throw new IllegalStateException("Changement de statut non autorisé");
        }

        mission.setStatut(newStatut);
        return missionRepository.save(mission);
    }
    private boolean isValidStatusTransition(MissionStatut current, MissionStatut next) {
        return switch (current) {
            case EN_ATTENTE -> next == MissionStatut.EN_NEGOCIATION;
            case EN_NEGOCIATION -> next == MissionStatut.ANNULEE || next == MissionStatut.ACCEPTEE;
            case ACCEPTEE -> next == MissionStatut.TERMINEE;
            case TERMINEE -> false;
            default -> false;
        };
    }
    public List<Mission> getMonitoredMissions() {
        //return missionRepository.findByIdBetween(41782L,41830L);
        return missionRepository.findByIdBetween(42194L,42239L);
    }

}
