package com.example.mcp.mcp.schema;

import java.util.List;
import java.util.Map;

/**
 * Local implementation of MCP Schema interfaces
 */
public class McpSchema {

    // Resource related classes
    public static class Resource {
        private final String uri;
        private final String name;
        private final String mimeType;
        private final String description;
        private final Map<String, Object> metadata;

        public Resource(String uri, String name, String mimeType, String description, Map<String, Object> metadata) {
            this.uri = uri;
            this.name = name;
            this.mimeType = mimeType;
            this.description = description;
            this.metadata = metadata;
        }

        public String getUri() {
            return uri;
        }

        public String getName() {
            return name;
        }

        public String getMimeType() {
            return mimeType;
        }

        public String getDescription() {
            return description;
        }

        public Map<String, Object> getMetadata() {
            return metadata;
        }
    }

    public static class ResourceTemplate {
        private final String pattern;
        private final String name;
        private final String description;

        public ResourceTemplate(String pattern, String name, String description) {
            this.pattern = pattern;
            this.name = name;
            this.description = description;
        }

        public String pattern() {
            return pattern;
        }

        public String name() {
            return name;
        }

        public String description() {
            return description;
        }
    }

    public static class GetResourceRequest {
        private final String uri;

        public GetResourceRequest(String uri) {
            this.uri = uri;
        }

        public String getUri() {
            return uri;
        }
    }

    public static class GetResourceResult {
        private final Object data;
        private final Error error;

        public GetResourceResult(Object data) {
            this.data = data;
            this.error = null;
        }

        public GetResourceResult(Error error) {
            this.data = null;
            this.error = error;
        }

        public Object getData() {
            return data;
        }

        public Error getError() {
            return error;
        }

        public boolean hasError() {
            return error != null;
        }
    }

    // Tool related classes
    public static class Tool {
        private final String name;
        private final String description;
        private final String schema;

        public Tool(String name, String description, String schema) {
            this.name = name;
            this.description = description;
            this.schema = schema;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getSchema() {
            return schema;
        }
    }

    public static class CallToolResult {
        private final Object data;
        private final Error error;

        public CallToolResult(Object data) {
            this.data = data;
            this.error = null;
        }

        public CallToolResult(Error error) {
            this.data = null;
            this.error = error;
        }

        public Object getData() {
            return data;
        }

        public Error getError() {
            return error;
        }

        public boolean hasError() {
            return error != null;
        }
    }

    // Prompt related classes
    public static class Prompt {
        private final String name;
        private final String description;
        private final String schema;

        public Prompt(String name, String description, String schema) {
            this.name = name;
            this.description = description;
            this.schema = schema;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getSchema() {
            return schema;
        }
    }

    public static class GetPromptRequest {
        private final Map<String, Object> arguments;

        public GetPromptRequest(Map<String, Object> arguments) {
            this.arguments = arguments;
        }

        public Map<String, Object> getArguments() {
            return arguments;
        }
    }

    public static class GetPromptResult {
        private final List<PromptMessage> messages;
        private final Error error;

        public GetPromptResult(List<PromptMessage> messages) {
            this.messages = messages;
            this.error = null;
        }

        public GetPromptResult(Error error) {
            this.messages = null;
            this.error = error;
        }

        public List<PromptMessage> getMessages() {
            return messages;
        }

        public Error getError() {
            return error;
        }

        public boolean hasError() {
            return error != null;
        }
    }

    public static class PromptMessage {
        private final Role role;
        private final Content content;

        public PromptMessage(Role role, Content content) {
            this.role = role;
            this.content = content;
        }

        public Role getRole() {
            return role;
        }

        public Content getContent() {
            return content;
        }
    }

    public interface Content {
        String getType();
        Object getValue();
    }

    public static class TextContent implements Content {
        private final String text;

        public TextContent(String text) {
            this.text = text;
        }

        @Override
        public String getType() {
            return "text";
        }

        @Override
        public Object getValue() {
            return text;
        }
    }

    public enum Role {
        SYSTEM, USER, ASSISTANT
    }

    // Common classes
    public static class Error {
        private final String code;
        private final String message;

        public Error(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

    // Server capabilities
    public static class ServerCapabilities {
        private final Object experimental;
        private final LoggingCapabilities logging;
        private final PromptCapabilities prompts;
        private final ResourceCapabilities resources;
        private final ToolCapabilities tools;

        public ServerCapabilities(Object experimental, LoggingCapabilities logging, 
                                 PromptCapabilities prompts, ResourceCapabilities resources, 
                                 ToolCapabilities tools) {
            this.experimental = experimental;
            this.logging = logging;
            this.prompts = prompts;
            this.resources = resources;
            this.tools = tools;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Object experimental;
            private LoggingCapabilities logging = new LoggingCapabilities();
            private PromptCapabilities prompts;
            private ResourceCapabilities resources;
            private ToolCapabilities tools;

            public Builder experimental(Object experimental) {
                this.experimental = experimental;
                return this;
            }

            public Builder logging(boolean enabled) {
                this.logging = new LoggingCapabilities(enabled);
                return this;
            }

            public Builder prompts(boolean streaming) {
                this.prompts = new PromptCapabilities(streaming);
                return this;
            }

            public Builder resources(boolean streaming, boolean write) {
                this.resources = new ResourceCapabilities(streaming, write);
                return this;
            }

            public Builder tools(boolean streaming) {
                this.tools = new ToolCapabilities(streaming);
                return this;
            }

            public ServerCapabilities build() {
                return new ServerCapabilities(experimental, logging, prompts, resources, tools);
            }
        }

        public static class LoggingCapabilities {
            private final boolean enabled;

            public LoggingCapabilities() {
                this.enabled = true;
            }

            public LoggingCapabilities(boolean enabled) {
                this.enabled = enabled;
            }

            public boolean isEnabled() {
                return enabled;
            }
        }

        public static class PromptCapabilities {
            private final boolean streaming;

            public PromptCapabilities(boolean streaming) {
                this.streaming = streaming;
            }

            public boolean isStreaming() {
                return streaming;
            }
        }

        public static class ResourceCapabilities {
            private final boolean streaming;
            private final boolean write;

            public ResourceCapabilities(boolean streaming, boolean write) {
                this.streaming = streaming;
                this.write = write;
            }

            public boolean isStreaming() {
                return streaming;
            }

            public boolean isWrite() {
                return write;
            }
        }

        public static class ToolCapabilities {
            private final boolean streaming;

            public ToolCapabilities(boolean streaming) {
                this.streaming = streaming;
            }

            public boolean isStreaming() {
                return streaming;
            }
        }
    }

    // Server implementation details
    public static class Implementation {
        private final String name;
        private final String version;

        public Implementation(String name, String version) {
            this.name = name;
            this.version = version;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }
    }
}
