import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
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
      maxWidth: '400px', 
      margin: '50px auto', 
      padding: '30px', 
      backgroundColor: '#fff', 
      borderRadius: '4px',
      border: '1px solid #ddd',
      boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
    }}>
      <h1>Log In</h1>
      
      {error && (
        <div className="error" style={{ 
          padding: '10px', 
          marginBottom: '15px', 
          backgroundColor: '#fee', 
          border: '1px solid #fcc',
          borderRadius: '4px',
          color: '#c33'
        }}>
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: '15px' }}>
          <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
            Email:
          </label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Enter your email"
            style={{ 
              width: '100%', 
              padding: '8px', 
              border: '1px solid #ddd', 
              borderRadius: '4px',
              fontSize: '14px'
            }}
            required
          />
        </div>

        <div style={{ marginBottom: '20px' }}>
          <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
            Password:
          </label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Enter your password"
            style={{ 
              width: '100%', 
              padding: '8px', 
              border: '1px solid #ddd', 
              borderRadius: '4px',
              fontSize: '14px'
            }}
            required
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          style={{
            width: '100%',
            padding: '10px',
            backgroundColor: '#007bff',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            fontSize: '16px',
            cursor: loading ? 'not-allowed' : 'pointer',
            opacity: loading ? 0.6 : 1
          }}
        >
          {loading ? 'Logging in...' : 'Log In'}
        </button>
      </form>

      <div style={{ marginTop: '20px', textAlign: 'center', fontSize: '14px', color: '#666' }}>
        <p>Need an account? Contact the administrator.</p>
        <p style={{ marginTop: '10px', fontSize: '12px' }}>
          Test credentials: john@example.com / password123
        </p>
      </div>
    </div>
  );
}

export default LoginForm;

