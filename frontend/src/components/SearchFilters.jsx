import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { searchEvents, getEvents } from '../services/api';

function SearchFilters() {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [filters, setFilters] = useState({
    keyword: '',
    category: '',
    date: '',
    location: ''
  });

  useEffect(() => {
    loadAllEvents();
  }, []);

  const loadAllEvents = async () => {
    try {
      const response = await getEvents();
      setEvents(response.data.events || []);
    } catch (err) {
      console.error(err);
    }
  };

  const handleSearch = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await searchEvents(filters);
      setEvents(response.data.events || []);
    } catch (err) {
      setError('Failed to search events');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleClear = () => {
    setFilters({
      keyword: '',
      category: '',
      date: '',
      location: ''
    });
    loadAllEvents();
  };

  const handleChange = (e) => {
    setFilters({
      ...filters,
      [e.target.name]: e.target.value
    });
  };

  return (
    <div>
      <h1>Filter Events</h1>
      
      <div style={{ marginBottom: '20px', padding: '15px', backgroundColor: '#fff', borderRadius: '4px' }}>
        <div style={{ marginBottom: '10px' }}>
          <label>Keyword: </label>
          <input
            type="text"
            name="keyword"
            value={filters.keyword}
            onChange={handleChange}
            placeholder="Search by name, category, or organizer"
            style={{ width: '300px' }}
          />
        </div>
        
        <div style={{ marginBottom: '10px' }}>
          <label>Category: </label>
          <select name="category" value={filters.category} onChange={handleChange}>
            <option value="">All Categories</option>
            <option value="Community">Community</option>
            <option value="Market">Market</option>
            <option value="Fitness">Fitness</option>
            <option value="Art">Art</option>
          </select>
        </div>
        
        <div style={{ marginBottom: '10px' }}>
          <label>Date: </label>
          <input
            type="date"
            name="date"
            value={filters.date}
            onChange={handleChange}
          />
        </div>
        
        <div style={{ marginBottom: '10px' }}>
          <label>Location: </label>
          <input
            type="text"
            name="location"
            value={filters.location}
            onChange={handleChange}
            placeholder="Search by location"
          />
        </div>
        
        <button onClick={handleSearch} disabled={loading}>
          {loading ? 'Searching...' : 'Apply Filter'}
        </button>
        <button onClick={handleClear}>Clear Filter</button>
      </div>

      {error && <div className="error">{error}</div>}

      <div>
        <h2>Filtered Event List:</h2>
        {events.length === 0 ? (
          <div style={{ padding: '20px', textAlign: 'center' }}>
            <p>No events found.</p>
          </div>
        ) : (
          <div>
            {events.map(event => (
              <div 
                key={event.id} 
                style={{ 
                  border: '1px solid #ddd', 
                  borderRadius: '4px', 
                  padding: '15px', 
                  margin: '10px 0',
                  backgroundColor: '#fff'
                }}
              >
                <h3>{event.title}</h3>
                <p><strong>Date:</strong> {event.date} | <strong>Time:</strong> {event.time} | <strong>Location:</strong> {event.location}</p>
                {event.description && <p>{event.description}</p>}
                <Link to={`/events/${event.id}`}>
                  <button>View Details</button>
                </Link>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

export default SearchFilters;

