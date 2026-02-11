package com.example.freelance.controller;

import com.example.freelance.dto.FreelancerRecommendationDTO;
import com.example.freelance.service.RecommendationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecommendationController.class)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationService recommendationService;

    @Test
    @DisplayName("GET /api/recommandations/mission/{missionId} - cas nominal : retourne une liste de recommandations")
    void testGetRecommendations_ReturnsList() throws Exception {
        // GIVEN
        Long missionId = 1L;
        FreelancerRecommendationDTO recommendation1 = new FreelancerRecommendationDTO(10L, "Doe", "John", 15);
        FreelancerRecommendationDTO recommendation2 = new FreelancerRecommendationDTO(11L, "Smith", "Jane", 12);
        List<FreelancerRecommendationDTO> recommendations = List.of(recommendation1, recommendation2);
        when(recommendationService.recommendFreelancersForMission(missionId)).thenReturn(recommendations);

        // WHEN & THEN
        mockMvc.perform(get("/api/recommandations/mission/{missionId}", missionId).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                // Vérifie que la réponse contient deux éléments
                .andExpect(jsonPath("$", hasSize(2)))
                // Vérifie le contenu du premier élément
                .andExpect(jsonPath("$[0].freelancerId", is(10))).andExpect(jsonPath("$[0].nom", is("Doe"))).andExpect(jsonPath("$[0].prenom", is("John"))).andExpect(jsonPath("$[0].score", is(15.0)))
                // Vérifie le contenu du deuxième élément
                .andExpect(jsonPath("$[1].freelancerId", is(11))).andExpect(jsonPath("$[1].nom", is("Smith"))).andExpect(jsonPath("$[1].prenom", is("Jane"))).andExpect(jsonPath("$[1].score", is(12.0)));

        verify(recommendationService, times(1)).recommendFreelancersForMission(missionId);
    }

    @Test
    @DisplayName("GET /api/recommandations/mission/{missionId} - retourne une liste vide lorsqu'aucune recommandation n'est disponible")
    void testGetRecommendations_ReturnsEmptyList() throws Exception {
        // GIVEN
        Long missionId = 2L;
        when(recommendationService.recommendFreelancersForMission(missionId)).thenReturn(List.of());

        // WHEN & THEN
        mockMvc.perform(get("/api/recommandations/mission/{missionId}", missionId).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$", hasSize(0)));

        verify(recommendationService, times(1)).recommendFreelancersForMission(missionId);
    }
}
