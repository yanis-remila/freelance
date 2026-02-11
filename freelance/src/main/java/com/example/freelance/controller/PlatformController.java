package com.example.freelance.controller;
import com.example.freelance.model.Platform;
import com.example.freelance.service.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/platform")
public class PlatformController {

    @Autowired
    private PlatformService platformService;

    @PostMapping("/create")
    public Platform createPlatform() {
        return platformService.createPlatform();
    }

    @GetMapping("/{id}")
    public Optional<Platform> getPlatformById(@PathVariable Long id) {
        return platformService.getPlatformById(id);
    }




}