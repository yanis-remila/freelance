package com.example.freelance.controller;

import com.example.freelance.model.Evaluation;
import com.example.freelance.service.EvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping("/add")
    public ResponseEntity<Evaluation> createEvaluation(
            @RequestBody Evaluation evaluation,
            @RequestParam Long missionId) {

        Evaluation savedEvaluation = evaluationService.saveEvaluation(evaluation, missionId);
        return ResponseEntity.ok(savedEvaluation);
    }

    @GetMapping("/warning")
    public ResponseEntity<List<Evaluation>> getWarningMissions() {
        List<Evaluation> warningMissions = evaluationService.getWarningMissions();
        if (warningMissions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(warningMissions);
    }

    @GetMapping("/well-rated")
    public ResponseEntity<List<Evaluation>> getWellRatedMissions() {
        List<Evaluation> wellRatedMissions = evaluationService.getWellRatedMissions();
        if (wellRatedMissions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(wellRatedMissions);
    }


}
