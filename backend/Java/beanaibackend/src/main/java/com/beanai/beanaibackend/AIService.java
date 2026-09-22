package com.beanai.beanaibackend;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

//Service for handling ai
@Service 
public class AIService {
    //Initialze RestClient and build the Client
    private final RestClient aiClient = RestClient.builder()
        .baseUrl("http://localhost:8000")
        .defaultHeader("Accept", "application/json")
        .build();

    //Method to preform get request
    public AIResponseDTO getAIResponse(AIRequestDTO requestDTO){
        return aiClient.post()
            .uri("/ai")
            .body(requestDTO)
            .retrieve()
            .body(AIResponseDTO.class);
    }
}
