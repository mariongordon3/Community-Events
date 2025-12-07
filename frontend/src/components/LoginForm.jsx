import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { login, logout } from '../services/api';

function LoginForm({ onLogin }) {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!email.trim() || !password.trim()) {
      setError('Email and password are required');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      
      const response = await login(email, password);
      
      if (response.data.success && response.data.user) {
        onLogin(response.data.user);
        navigate('/');
      } else {
        setError('Invalid credentials');
      }
    } catch (err) {
      const errorMessage = err.response?.data?.error || 'Invalid credentials';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = async () => {
    try {
      await logout();
      onLogin(null);
      navigate('/');
    } catch (err) {
      console.error('Logout error:', err);
    }
  };

  return (
    <div style={{ 
      maxWidth: '450px', 
      margin: '50px auto'
    }}>
      <div className="card">
        <h1>Log In</h1>
        
        {error && <div className="error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: '20px' }}>
            <label>Email</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="Enter your email"
              required
            />
          </div>

          <div style={{ marginBottom: '25px' }}>
            <label>Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter your password"
              required
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            style={{ width: '100%', padding: '12px', fontSize: '16px' }}
          >
            {loading ? 'Logging in...' : 'Log In'}
          </button>
        </form>

        <div style={{ marginTop: '25px', textAlign: 'center', fontSize: '14px', color: '#666', paddingTop: '20px', borderTop: '1px solid #e9ecef' }}>
          <p>Don't have an account? <Link to="/register" style={{ color: '#007bff', textDecoration: 'none', fontWeight: '500' }}>Register here</Link></p>
          <p style={{ marginTop: '15px', fontSize: '12px', color: '#999' }}>
            Test credentials: john@example.com / password123
          </p>
        </div>
      </div>
    </div>
  );
}

export default LoginForm;

