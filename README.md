# Devin Practice - Calligraphy Art Store

## Local LLM Setup with Ollama (Recommended for China)

To use the chat feature with a local LLM deployment:

1. Install Ollama:
   ```bash
   curl -fsSL https://ollama.com/install.sh | sh
   ```

2. Start Ollama:
   ```bash
   ollama serve
   ```

3. Pull a model (in a separate terminal):
   ```bash
   ollama pull llama2
   ```
   
   You can also try other models like:
   ```bash
   ollama pull qwen:0.5b
   # or
   ollama pull phi
   ```

4. Update application.properties (if needed):
   ```properties
   llm.api.url=http://localhost:11434/v1/chat/completions
   llm.api.key=
   llm.api.model=llama2  # or the model you pulled
   ```

5. Start the application and test the chat feature

## Troubleshooting

If you encounter issues with Ollama:
- Check if Ollama is running with `ps aux | grep ollama`
- Verify the model is downloaded with `ollama list`
- Check Ollama logs for errors
- The application will fall back to simulated responses if Ollama is unavailable

## Alternative API Options

If you prefer not to run a local LLM, you can use alternative API endpoints by updating the application.properties file:

```properties
# ChatAnywhere API (may require VPN access)
llm.api.url=https://api.chatanywhere.com.cn/v1/chat/completions
llm.api.key=your_api_key_here
llm.api.model=gpt-3.5-turbo
```

## Running the Application

To run the application:

```bash
cd ~/repos/devinPractice/devin
mvn spring-boot:run
```

The application will be available at http://localhost:8081
