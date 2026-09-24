package com.beanai.beanaibackend;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service 
public class AIService {

    private final WebClient aiClient = WebClient.builder()
        .baseUrl("http://127.0.0.1:8000")
        .defaultHeader("Accept", MediaType.TEXT_EVENT_STREAM_VALUE)
        .build();

    public Flux<String> getAIResponse(AIRequestDTO requestDTO){
        ParameterizedTypeReference<ServerSentEvent<String>> typeRef = 
                new ParameterizedTypeReference<>() {};

        return aiClient.post()
            .uri("/ai")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestDTO)
            .retrieve()
            .bodyToFlux(typeRef)

            //SAFETY CRITICAL: Filter out events that contain no data payload first (like pings or empty structures)
            .filter(event -> event.data() != null)
            .map(event -> java.util.Objects.requireNonNull(event.data()))
            
            //Filter out empty string tokens if necessary
            .filter(text -> !text.isEmpty());
    }
}
