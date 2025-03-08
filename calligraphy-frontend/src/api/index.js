import axios from 'axios';

const API_URL = 'http://localhost:8081/api';

// Create axios instance with base URL
const api = axios.create({
  baseURL: API_URL
});

// Add request interceptor to include auth token
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (username, password) => {
    return api.post('/auth/login', { username, password });
  },
  register: (username, password) => {
    return api.post('/auth/register', { username, password });
  }
};

// Products API
export const productsAPI = {
  getAll: () => {
    return api.get('/products');
  },
  getById: (id) => {
    return api.get(`/products/${id}`);
  },
  getByCategory: (category) => {
    return api.get(`/products/category/${category}`);
  },
  getByPriceRange: (min, max) => {
    return api.get(`/products/price?min=${min}&max=${max}`);
  },
  getPopular: (limit) => {
    return api.get(`/products/popular/${limit}`);
  },
  create: (product) => {
    return api.post('/products', product);
  },
  update: (id, product) => {
    return api.put(`/products/${id}`, product);
  },
  delete: (id) => {
    return api.delete(`/products/${id}`);
  }
};

export default {
  auth: authAPI,
  products: productsAPI
};
