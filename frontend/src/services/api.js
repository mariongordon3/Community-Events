import axios from 'axios';

const API_BASE_URL = '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true, // Important for session cookies
  headers: {
    'Content-Type': 'application/json',
  },
});

// Event APIs
export const getEvents = () => api.get('/events');
export const searchEvents = (filters) => api.get('/events/search', { params: filters });
export const getEventDetails = (eventId) => api.get(`/events/${eventId}`);
export const getEventComments = (eventId) => api.get(`/events/${eventId}/comments`);
export const createEvent = (eventData) => api.post('/events', eventData);
export const deleteEvent = (eventId) => api.delete(`/events/${eventId}`);

// Comment APIs
export const addComment = (eventId, text) => api.post(`/events/${eventId}/comments`, { text });
export const editComment = (commentId, text) => api.put(`/comments/${commentId}`, { text });
export const deleteComment = (commentId) => api.delete(`/comments/${commentId}`);

// Auth APIs
export const login = (email, password) => api.post('/auth/login', { email, password });
export const register = (name, email, password) => api.post('/auth/register', { name, email, password });
export const logout = () => api.post('/auth/logout');
export const getAuthStatus = () => api.get('/auth/status');

export default api;

