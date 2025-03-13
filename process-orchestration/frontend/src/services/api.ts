import axios from 'axios';

// API base URL - can be configured for different environments
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

// Create axios instance with default config
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Node types API
export const getNodeTypes = () => {
  return api.get('/node-types');
};

// Process definition API
export const getProcessDefinitions = () => {
  return api.get('/process-definitions');
};

export const getProcessDefinition = (id: number) => {
  return api.get(`/process-definitions/${id}`);
};

export const createProcessDefinition = (processDefinition: any) => {
  return api.post('/process-definitions', processDefinition);
};

export const updateProcessDefinition = (id: number, processDefinition: any) => {
  return api.put(`/process-definitions/${id}`, processDefinition);
};

export const deleteProcessDefinition = (id: number) => {
  return api.delete(`/process-definitions/${id}`);
};

// Process execution API
export const executeProcess = (id: number, waitForCompletion = false, timeoutSeconds = 30) => {
  return api.post(`/process-executions/${id}?waitForCompletion=${waitForCompletion}&timeoutSeconds=${timeoutSeconds}`);
};

export const getProcessStatus = (id: number) => {
  return api.get(`/process-executions/${id}`);
};

export const stopProcess = (id: number) => {
  return api.delete(`/process-executions/${id}`);
};

export default api;
