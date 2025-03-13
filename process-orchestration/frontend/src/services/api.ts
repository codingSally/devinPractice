import axios from 'axios';

// Create axios instance with default config
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // 10 seconds timeout
});

// Add response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response || error.message);
    return Promise.reject(error);
  }
);

// Create a new process definition
export const createProcessDefinition = (processDefinition: any) => {
  return api.post('/processes', processDefinition);
};

// Execute a process
export const executeProcess = (processId: number) => {
  return api.post(`/processes/${processId}/execute`);
};

// Get all process definitions
export const getProcessDefinitions = () => {
  return api.get('/processes');
};

// Get a specific process definition
export const getProcessDefinition = (processId: number) => {
  return api.get(`/processes/${processId}`);
};

// Get all node types
export const getNodeTypes = () => {
  return api.get('/node-types');
};
