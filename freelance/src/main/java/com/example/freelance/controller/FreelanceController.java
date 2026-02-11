package com.example.freelance.controller;

import com.example.freelance.dto.FreelancerStatsDTO;
import com.example.freelance.model.Freelancer;
import com.example.freelance.model.Mission;
import com.example.freelance.model.enums.MissionStatut;
import com.example.freelance.service.FreelanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.example.freelance.repository.FreelancerRepository;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/freelances")
public class FreelanceController {
    private final FreelanceService freelanceService;
    private final FreelancerRepository freelancerRepository;
    @GetMapping
    public List<Freelancer> getAllFreelances() {
        return freelanceService.getAllFreelances();
    }

    @GetMapping("/{id}")
    public Freelancer getFreelanceById(@PathVariable Long id) {
        return freelanceService.getFreelanceById(id);
    }

    @PostMapping
    public Freelancer saveFreelance(@RequestBody Freelancer freelance) {
        return freelanceService.saveFreelance(freelance);
    }

    @DeleteMapping("/{id}")
    public void deleteFreelance(@PathVariable Long id) {
        freelanceService.deleteFreelance(id);
    }

    @DeleteMapping("/test/{name}")
    public void deleteFreelanceByName(@PathVariable String name) {

    }

    @GetMapping("/email/{email}")
    public Freelancer getFreelanceByEmail(@PathVariable String email) {
        return freelanceService.getFreelanceByEmail(email);
    }

    @PutMapping("/{id}")
    public Freelancer updateFreelance(@PathVariable Long id, @RequestBody Freelancer freelance) {
        return freelanceService.updateFreelance(id, freelance);
    }
    @GetMapping("/freelancers/stats")
    public List<FreelancerStatsDTO> getFreelancerStats() {
        List<Freelancer> freelancers = freelancerRepository.findAll();

        return freelancers.stream().map(f -> {
            long totalJours = f.getMissions().stream()
                    .filter(m -> m.getStatut() == MissionStatut.TERMINEE)
                    .mapToLong(m -> {
                        try {
                            return Long.parseLong(m.getDuree());
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    }).sum();

            double montantVerse = f.getMissions().stream()
                    .filter(m -> m.getStatut() == MissionStatut.TERMINEE)
                    .mapToDouble(Mission::getBudget)
                    .sum();

            double montantPayeParClient = montantVerse * 1.1;

            return new FreelancerStatsDTO(
                    f.getId(),
                    f.getNom(),
                    f.getPrenom(),
                    totalJours,
                    montantVerse,
                    montantPayeParClient,
                    montantPayeParClient - montantVerse
            );
        }).toList();
    }

}
