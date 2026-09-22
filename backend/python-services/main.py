from fastapi import FastAPI
from ollama import chat, ChatResponse #import ollama library
from pydantic import BaseModel

class request_data(BaseModel):
    prompt : str

app = FastAPI()

#Map the ai endpoint and take in incoming prompt as param
@app.post("/ai")
#Method to send prompt to ollama and return the response
async def ask_ai(incoming_prompt : request_data):
    response: ChatResponse = chat(
        model = "qwen2.5:3b",
        messages = [
            {
                'role' : 'user',
                'content' : incoming_prompt.prompt,
            },
        ],
    )
    return {"response" : response.message.content}

    