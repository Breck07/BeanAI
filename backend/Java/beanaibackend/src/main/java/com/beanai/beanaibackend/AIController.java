package com.beanai.beanaibackend;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


//Controller class for the AI service
@RestController 
public class AIController {
    private AIService service;

    //Inject the service bean into the controller object
    public AIController(AIService service){
        this.service = service;
    }

    //Map the POST endpoint
    @PostMapping("/beanllm")
    public String getResponse(@RequestBody AIRequestDTO request) {
        AIResponseDTO responseDTO = service.getAIResponse(request);
        return responseDTO.getResponse();
    }
    

}
