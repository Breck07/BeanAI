from fastapi import FastAPI
from fastapi.responses import StreamingResponse
from ollama import chat, ChatResponse, AsyncClient 
import ollama
from pydantic import BaseModel

class request_data(BaseModel):
    prompt : str

app = FastAPI()

@app.post("/ai")
async def ask_ai(incoming_prompt : request_data):

    client = AsyncClient()
    
    async def stream_generator():
        response_stream = await client.chat(
            model = "bean-ai",
            messages = [
                {
                    'role' : 'user',
                    'content' : incoming_prompt.prompt,
                },
            ],
            stream=True
        )
        async for chunk in response_stream:
            content = chunk.message.content
            if content: # Only send if the token isn't empty
                # Format exactly as Server-Sent Events (SSE)
                yield f"data: {content}\n\n"

    # Changed media_type to text/event-stream
    return StreamingResponse(stream_generator(), media_type="text/event-stream")
