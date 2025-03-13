import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

// Create a new process definition
export const createProcessDefinition = (processDefinition: any) => {
  return axios.post(`${API_BASE_URL}/processes`, processDefinition);
};

// Execute a process
export const executeProcess = (processId: number) => {
  return axios.post(`${API_BASE_URL}/processes/${processId}/execute`);
};

// Get all process definitions
export const getProcessDefinitions = () => {
  return axios.get(`${API_BASE_URL}/processes`);
};

// Get a specific process definition
export const getProcessDefinition = (processId: number) => {
  return axios.get(`${API_BASE_URL}/processes/${processId}`);
};

// Get all node types
export const getNodeTypes = () => {
  return axios.get(`${API_BASE_URL}/node-types`);
};
