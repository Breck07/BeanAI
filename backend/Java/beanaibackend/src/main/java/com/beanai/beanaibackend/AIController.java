package com.beanai.beanaibackend;

import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;



//Controller class for the AI service
@RestController
@RequestMapping("/model") 
public class AIController {
    private AIService service;

    //Inject the service bean into the controller object
    public AIController(AIService service){
        this.service = service;
    }

    //Map the POST endpoint
    @PostMapping(value = "/beanllm", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getResponse(@RequestBody AIRequestDTO request) {
        return service.getAIResponse(request);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getMethodName() {
        //Initialize map for status data
        Map<String, String> status = new LinkedHashMap<>();
        status.put("status", "UP");
        status.put("service", "API");

        return ResponseEntity.ok(status);
        
    }
}
