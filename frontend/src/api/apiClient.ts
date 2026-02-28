import axios from 'axios';

const apiClient = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000,
});

// Request interceptor
apiClient.interceptors.request.use(
  (config) => {
    // Attach auth token here if needed in future
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      const { status, data } = error.response;
      const message = data?.message || 'An unexpected error occurred.';
      console.error(`[API Error] Status: ${status}, Message: ${message}`);
    } else if (error.request) {
      console.error('[API Error] No response received. Is the server running?');
    } else {
      console.error('[API Error] Request setup failed:', error.message);
    }
    return Promise.reject(error);
  }
);

export default apiClient;
