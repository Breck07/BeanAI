package com.beanai.beanaibackend;

//DTO class for ai response
public class AIResponseDTO {
    private String response;

    //Initialize AIResponseDTO object
    public AIResponseDTO(){

    }

    //Getters and Setters
    public void setResponse(String response){
        this.response = response;
    }
    public String getResponse(){
        return response;
    }

}
