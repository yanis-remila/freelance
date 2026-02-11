package com.example.freelance.service;
import com.example.freelance.model.Freelancer;
import com.example.freelance.model.MissionFreelance;
import com.example.freelance.repository.MissionFreelanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FreelanceServiceTest {

    @InjectMocks
    private FreelanceService freelanceService;

    @Mock
    private MissionFreelanceRepository missionFreelanceRepository;

    @Test
    public void testFreelancerIsAvailable() {

        Freelancer freelancer = new Freelancer();
        freelancer.setId(1L);

        when(missionFreelanceRepository.findByFreelancerId(freelancer.getId())).thenReturn(Collections.emptyList());


        boolean result = freelanceService.isFreelancerAvailable(freelancer);


        assertTrue(result, "Freelancer should be available.");
    }

    @Test
    public void testFreelancerIsNotAvailable() {

        Freelancer freelancer = new Freelancer();
        freelancer.setId(1L);


        MissionFreelance missionFreelance = new MissionFreelance();
        when(missionFreelanceRepository.findByFreelancerId(freelancer.getId())).thenReturn(Collections.singletonList(missionFreelance));


        boolean result = freelanceService.isFreelancerAvailable(freelancer);


        assertFalse(result, "Freelancer should not be available.");
    }
}

