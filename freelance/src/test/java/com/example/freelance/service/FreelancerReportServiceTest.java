package com.example.freelance.service;

import com.example.freelance.model.Freelancer;
import com.example.freelance.model.FreelancerReport;
import com.example.freelance.model.Mission;
import com.example.freelance.model.enums.MissionStatut;
import com.example.freelance.repository.FreelancerReportRepository;
import com.example.freelance.repository.FreelancerRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FreelancerReportServiceTest {

    @Test
    void test_generate_reports_successfully() {
        Freelancer freelancer = new Freelancer();

        Mission mission1 = new Mission();
        mission1.setStatut(MissionStatut.TERMINEE);
        mission1.setDuree("10");
        mission1.setBudget(1000.0);

        Mission mission2 = new Mission();
        mission2.setStatut(MissionStatut.TERMINEE);
        mission2.setDuree("5");
        mission2.setBudget(2000.0);

        freelancer.setMissions(Set.of(mission1, mission2));

        FreelancerRepository freelancerRepo = mock(FreelancerRepository.class);
        FreelancerReportRepository reportRepo = mock(FreelancerReportRepository.class);

        when(freelancerRepo.findAll()).thenReturn(List.of(freelancer));
        when(reportRepo.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        FreelancerReportService service = new FreelancerReportService(freelancerRepo, reportRepo);

        List<FreelancerReport> reports = service.generateAndSaveReports();

        assertEquals(1, reports.size());
        FreelancerReport report = reports.get(0);
        assertEquals(15, report.getJoursTravailles());
        assertEquals(3000.0, report.getMontantVerse(), 0.0001);
        assertEquals(3300.0, report.getMontantPayeClient(), 0.0001);
        assertEquals(300.0, report.getProfit(), 0.0001);
    }

    @Test
    void test_generate_reports_when_saving_fails() {
        FreelancerRepository freelancerRepo = mock(FreelancerRepository.class);
        FreelancerReportRepository reportRepo = mock(FreelancerReportRepository.class);

        Freelancer freelancer = new Freelancer();
        freelancer.setMissions(Set.of());

        when(freelancerRepo.findAll()).thenReturn(List.of(freelancer));
        when(reportRepo.saveAll(anyList())).thenThrow(new RuntimeException("Erreur de sauvegarde"));

        FreelancerReportService service = new FreelancerReportService(freelancerRepo, reportRepo);

        RuntimeException ex = assertThrows(RuntimeException.class, service::generateAndSaveReports);
        assertEquals("Erreur de sauvegarde", ex.getMessage());
    }
}
