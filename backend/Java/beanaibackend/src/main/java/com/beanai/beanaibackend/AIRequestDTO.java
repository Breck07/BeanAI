package com.beanai.beanaibackend;

//DTO class for incoming AI request
public class AIRequestDTO {
    private String prompt;

    //Initialize AIRequestDTO object
    public AIRequestDTO(){
    }

    //Getters and Setters
    public void setPrompt(String prompt){
        this.prompt = prompt;
    }
    public String getPrompt(){
        return prompt;
    }

}
