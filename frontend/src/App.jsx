import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Link } from 'react-router-dom';
import EventList from './components/EventList';
import EventDetails from './components/EventDetails';
import EventForm from './components/EventForm';
import LoginForm from './components/LoginForm';
import RegisterForm from './components/RegisterForm';
import SearchFilters from './components/SearchFilters';
import { getAuthStatus, logout } from './services/api';

function App() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    checkAuthStatus();
  }, []);

  const checkAuthStatus = async () => {
    try {
      const response = await getAuthStatus();
      if (response.data.isLoggedIn) {
        setUser(response.data.user);
      }
    } catch (error) {
      console.error('Error checking auth status:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleLogin = (userData) => {
    setUser(userData);
  };

  const handleLogout = async () => {
    try {
      await logout();
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      setUser(null);
    }
  };

  if (loading) {
    return <div className="container">Loading...</div>;
  }

  return (
    <Router>
      <div className="container">
        <nav>
          <div style={{ display: 'flex', alignItems: 'center', gap: '20px', flexWrap: 'wrap' }}>
            <Link to="/">Home</Link>
            <Link to="/search">Search</Link>
            {user && <Link to="/create-event">Create Event</Link>}
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
            {user ? (
              <>
                <span>Welcome, {user.name}!</span>
                <Link to="/login" onClick={handleLogout}>Log Out</Link>
              </>
            ) : (
              <Link to="/login">Log In</Link>
            )}
          </div>
        </nav>

        <Routes>
          <Route path="/" element={<EventList />} />
          <Route path="/search" element={<SearchFilters />} />
          <Route path="/events/:id" element={<EventDetails user={user} />} />
          <Route 
            path="/events/:id/edit" 
            element={user ? <EventForm user={user} /> : <Navigate to="/login" />} 
          />
          <Route 
            path="/create-event" 
            element={user ? <EventForm user={user} /> : <Navigate to="/login" />} 
          />
          <Route 
            path="/login" 
            element={user ? <Navigate to="/" /> : <LoginForm onLogin={handleLogin} />} 
          />
          <Route 
            path="/register" 
            element={user ? <Navigate to="/" /> : <RegisterForm onLogin={handleLogin} />} 
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;

