package com.example.freelance.controller;

import com.example.freelance.model.Client;
import com.example.freelance.model.Mission;
import com.example.freelance.repository.ClientRepository;
import com.example.freelance.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientRepository clientRepository;
    private final MissionRepository missionRepository;

    @GetMapping
    public ResponseEntity<Client> findClientByNom(@RequestParam String nom) {
        return clientRepository.findByNom(nom)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Client> findClientByEmail(@RequestParam String email) {
        return clientRepository.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{idClient}/mission")
    public ResponseEntity<List<Mission>> findMissionByClientId(@PathVariable Long idClient) {
        List<Mission> missions = missionRepository.findByClientId(idClient);
        return ResponseEntity.ok(missions);
    }
}
