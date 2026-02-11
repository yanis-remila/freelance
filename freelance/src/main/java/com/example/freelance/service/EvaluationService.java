package com.example.freelance.service;

import com.example.freelance.model.Evaluation;
import com.example.freelance.model.Mission;
import com.example.freelance.repository.EvaluationRepository;
import com.example.freelance.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    @Autowired
    private MissionRepository missionRepository;
    public EvaluationService(EvaluationRepository evaluationRepository) {
        this.evaluationRepository = evaluationRepository;
    }
public Evaluation saveEvaluation(Evaluation evaluation, Long missionId) {

    Mission mission = missionRepository.findById(missionId)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée"));


    if (mission.getFreelancer() == null) {
        throw new RuntimeException("Mission does not have an assigned freelancer.");
    }


    evaluation.setMission(mission);
    evaluation.setDateEvaluation(LocalDateTime.now());

    return evaluationRepository.save(evaluation);
}
    public List<Evaluation> getWarningMissions() {
        List<Evaluation> warnings = evaluationRepository.findAllByNoteLessThanEqual(3.0);
        return warnings.isEmpty() ? List.of() : warnings;
    }

    public List<Evaluation> getWellRatedMissions() {
        List<Evaluation> wellRated = evaluationRepository.findAllByNoteGreaterThan(3.0);
        return wellRated.isEmpty() ? List.of() : wellRated;
    }

}
