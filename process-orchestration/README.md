# Process Orchestration System

A complete process orchestration system with a Spring Boot backend and React frontend, featuring flexible process node execution, breadth-first traversal with parallel execution, and a drag-and-drop UI for process configuration.

## Features

### Backend (Spring Boot)

- Flexible process node framework with extensible node types
- Breadth-first traversal with parallel execution for nodes at the same level
- RESTful API for process definition and execution
- Support for multiple node types: logging, HTTP, script, and conditional

### Frontend (React)

- Drag-and-drop interface for process configuration
- Visual process node connections
- Process execution and monitoring
- Integration with backend API

## Setup Instructions

### Backend Setup

1. Navigate to the backend directory:
   ```
   cd process-orchestration/backend
   ```

2. Build the project:
   ```
   mvn clean install
   ```

3. Run the application:
   ```
   mvn spring-boot:run
   ```

4. The backend will be available at `http://localhost:8080/api`

### Frontend Setup

1. Navigate to the frontend directory:
   ```
   cd process-orchestration/frontend
   ```

2. Install dependencies:
   ```
   npm install
   ```

3. Start the development server:
   ```
   npm start
   ```

4. The frontend will be available at `http://localhost:3000`

## API Endpoints

### Node Types

- `GET /api/node-types`: Get all available node types

### Process Definitions

- `POST /api/process-definitions`: Create a new process definition
- `GET /api/process-definitions`: Get all process definitions
- `GET /api/process-definitions/{id}`: Get a process definition by ID
- `PUT /api/process-definitions/{id}`: Update a process definition
- `DELETE /api/process-definitions/{id}`: Delete a process definition

### Process Executions

- `POST /api/process-executions/{id}`: Execute a process
- `GET /api/process-executions/{id}`: Get process execution status
- `DELETE /api/process-executions/{id}`: Stop a running process

## Usage Example

1. Create a process definition with the drag-and-drop interface
2. Save the process definition
3. Execute the process
4. Monitor the process execution status

## Node Types

1. **Logging Node**: Logs a message to the console
2. **HTTP Node**: Makes an HTTP request
3. **Script Node**: Executes a JavaScript script
4. **Conditional Node**: Evaluates a condition and determines which child nodes to execute
