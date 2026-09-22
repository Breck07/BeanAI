package com.beanai.beanaibackend;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;

//This class runs external services such as the python api on launch
@Component 
public class ExternalServiceRunner {
    //Initialize log
    private static final Logger log = LoggerFactory.getLogger(ExternalServiceRunner.class);
    
    //Initialze path to python api
    private Path pythonPath = Path.of("../../python-services");

    //Initialze the process variables
    private Process ollamaEngine;
    private Process pythonProcess;

    @EventListener(ApplicationReadyEvent.class)
        public void onApplicationReady(){
            runOllama();
            
            //Wait for ollama model to run before creating api
            boolean isRunning = false;
            int tries = 0;

            while(!isRunning && tries < 10){
                isRunning = checkService();
                if(!isRunning){
                    try{
                        tries++;
                        TimeUnit.MILLISECONDS.sleep(500);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            } 

            //Check if file exists
            if(!Files.exists(pythonPath)){
                log.error("Python service not found!");
                return;
            }

            //Handle os
            String os = System.getProperty("os.name").toLowerCase();
            Path uvicorn = os.contains("win")
            ? pythonPath.resolve("venv").resolve("Scripts").resolve("uvicorn.exe")
            : pythonPath.resolve("venv").resolve("bin").resolve("uvicorn");

            runPythonAPI(uvicorn);
        }

    //Method to start the python api
    private void runPythonAPI(Path uvicorn){
        List<String> commands = new ArrayList<>();
        commands.add(uvicorn.toAbsolutePath().toString());
        commands.add("main:app");
        commands.add("--host");
        commands.add("127.0.0.1");
        commands.add("--port");
        commands.add("8000");

        //Build process, ensure to log output
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.directory(pythonPath.toFile());
        processBuilder.inheritIO();
        
        //Try to run process and handle exceptions
        try{
            pythonProcess = processBuilder.start();
        }catch(Exception e){
            log.error("Error running the process for runnning python api service!");
        }
    }
    private void runOllama(){
        //Check if ollama engine is already running, if not then start it
        boolean isRunning = checkService();
        if(!isRunning){
            //Process to run ollama engine on application launch
            ProcessBuilder pb = new ProcessBuilder("ollama", "serve");
            pb.inheritIO();

            try{
                ollamaEngine = pb.start();
            }catch(Exception e){
                log.error("Error with starting ollama engine!");
            }
        }else{
            return;
        }
    }

    //Method to check the ollama engine at port 11434
    private boolean checkService(){
        String host = "localhost";
        int port = 11434;
        int timeout = 1500;
        try(Socket socket = new Socket()){
            socket.connect(new InetSocketAddress(host,port), timeout);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @PreDestroy
    public void endOllama(){
        //Check if process is still running and not null
        if(ollamaEngine != null && ollamaEngine.isAlive()){
            ollamaEngine.destroy(); //Cleanly destory process

            try{
                //Wait for process to shutdown
                ollamaEngine.waitFor(5, TimeUnit.SECONDS);
                if(ollamaEngine.isAlive()){
                    //Force shutdown after 5 seconds
                    ollamaEngine.destroyForcibly();
                }
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }

            log.info("Ollama engine has shutdown.");
        }
    }

    //Destory the process when spring application stops
    @PreDestroy
    public void endFastAPI(){
        //Check if process is still alive and not empty
        if(pythonProcess != null && pythonProcess.isAlive()){
            pythonProcess.destroy(); //Clean destory

            try{
                //Wait for process to fully shutdown
                pythonProcess.waitFor(5, TimeUnit.SECONDS);
                if(pythonProcess.isAlive()){
                    //Force shutdown if its still not destroyed after 5 seconds
                    pythonProcess.destroyForcibly();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            log.info("The fastapi service for bean ai llm has shutdown.");
        }
    }
}
