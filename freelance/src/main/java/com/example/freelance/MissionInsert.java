//package com.example.freelance;
//
//import com.example.freelance.service.MissionService;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class MissionInsert implements CommandLineRunner {
//
//    private final MissionService missionService;
//
//    public MissionInsert(MissionService missionService) {
//        this.missionService = missionService;
//    }
//
//    public static void main(String[] args) {
//        SpringApplication.run(MissionInsert.class, args);
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Créer les missions de test
//        missionService.createTestMissions();
//        System.out.println("2 missions créées avec succès !");
//    }
//}
