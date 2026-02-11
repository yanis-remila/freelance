package com.example.freelance.controller;

import com.example.freelance.model.Mission;
import com.example.freelance.model.enums.MissionStatut;
import com.example.freelance.service.FreelanceService;
import com.example.freelance.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/missions")
public class MissionController {
    @Autowired
    private FreelanceService freelanceService;

    private final MissionService missionService;

    @PostMapping("/post")
    public Mission postMachine(@RequestBody Mission mission) {
        System.out.println(mission);
        return missionService.saveMission(mission);
    }

    @PutMapping("/put/{id}")
    public Mission putMachine(@PathVariable long id, @RequestParam(name = "status", required = true) MissionStatut status) {
        return missionService.updateMissionStatus(id, status);
    }

    @GetMapping("/{id}")
    public Mission getMissionById(@PathVariable Long id) {
        return freelanceService.getMissionWithCompetences(id);
    }

    @GetMapping("/monitored")
    public List<Mission> getMonitoredMissions() {
        return missionService.getMonitoredMissions();
    }
}
