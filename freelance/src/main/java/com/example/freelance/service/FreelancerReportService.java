package com.example.freelance.service;

import com.example.freelance.model.Freelancer;
import com.example.freelance.model.FreelancerReport;
import com.example.freelance.model.Mission;
import com.example.freelance.model.enums.MissionStatut;
import com.example.freelance.repository.FreelancerReportRepository;
import com.example.freelance.repository.FreelancerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FreelancerReportService {

    private final FreelancerRepository freelancerRepository;
    private final FreelancerReportRepository reportRepository;

    public List<FreelancerReport> generateAndSaveReports() {
        List<Freelancer> freelancers = freelancerRepository.findAll();

        List<FreelancerReport> reports = freelancers.stream().map(f -> {
            long jours = f.getMissions().stream()
                    .filter(m -> m.getStatut() == MissionStatut.TERMINEE)
                    .mapToLong(m -> {
                        try {
                            return Long.parseLong(m.getDuree());
                        } catch (NumberFormatException e) {
                            return 0L;
                        }
                    }).sum();

            double verse = f.getMissions().stream()
                    .filter(m -> m.getStatut() == MissionStatut.TERMINEE)
                    .mapToDouble(Mission::getBudget)
                    .sum();

            double paye = verse * 1.1;
            double profit = paye - verse;

            FreelancerReport report = new FreelancerReport();
            report.setFreelancer(f);
            report.setJoursTravailles(jours);
            report.setMontantVerse(verse);
            report.setMontantPayeClient(paye);
            report.setProfit(profit);
            return report;
        }).collect(Collectors.toList());

        return reportRepository.saveAll(reports);
    }

    public List<FreelancerReport> getAllReports() {
        return reportRepository.findAll();
    }
}
