#!/bin/bash
echo "Testing Ollama API..."
curl -X POST http://localhost:11434/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "model": "llama2",
    "messages": [
      {
        "role": "system",
        "content": "You are a helpful customer service assistant for a calligraphy art store."
      },
      {
        "role": "user",
        "content": "Tell me about your calligraphy brushes."
      }
    ]
  }'
echo -e "\n\nIf you see a JSON response with 'content' field, Ollama is working correctly!"
