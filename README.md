# Devin Practice - Calligraphy Art Store

## About the Chat Feature

The application includes a chat feature that uses LLM (Large Language Model) APIs to provide intelligent responses about calligraphy products. The chat feature has been designed with multiple fallback mechanisms to ensure it works reliably even when API connections fail.

## LLM API Configuration Options

### Option 1: Local Ollama Deployment (Recommended for China)

For a completely local solution that works without internet access:

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
   ollama pull qwen:0.5b  # Chinese model
   # or
   ollama pull phi  # Smaller model
   ```

4. Update application.properties (if needed):
   ```properties
   llm.api.url=http://localhost:11434/v1/chat/completions
   llm.api.key=
   llm.api.model=llama2  # or the model you pulled
   ```

### Option 2: ChatAnywhere API

If you prefer not to run a local LLM:

```properties
llm.api.url=https://api.chatanywhere.com.cn/v1/chat/completions
llm.api.key=your_api_key_here
llm.api.model=gpt-3.5-turbo
```

## Robust Fallback Mechanism

The application now includes a robust fallback system:

1. **Multiple API Endpoints**: The application will automatically try multiple API endpoints if the configured one fails
2. **Intelligent Fallback Responses**: If all API endpoints fail, the application generates intelligent responses based on product information
3. **Fast Failover**: The application uses short timeouts to quickly switch between endpoints

## Troubleshooting

If you encounter issues with the chat feature:

### For Ollama:
- Check if Ollama is running with `ps aux | grep ollama`
- Verify the model is downloaded with `ollama list`
- Check Ollama logs for errors
- Try running the test script: `./test-ollama.sh`

### For ChatAnywhere:
- Check your network connection
- Verify your API key if you're using one
- Try a different API endpoint

### General Troubleshooting:
- Check the application logs for specific error messages
- The application will automatically use intelligent fallback responses if all API endpoints fail

## Running the Application

To run the application:

```bash
cd ~/repos/devinPractice/devin
mvn spring-boot:run
```

The application will be available at http://localhost:8081
