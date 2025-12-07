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
      
      <div className="card" style={{ marginBottom: '30px' }}>
        <h3 style={{ marginBottom: '20px' }}>Search Filters</h3>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '15px', marginBottom: '20px' }}>
          <div>
            <label>Keyword</label>
            <input
              type="text"
              name="keyword"
              value={filters.keyword}
              onChange={handleChange}
              placeholder="Search by name, category, or organizer"
            />
          </div>
          
          <div>
            <label>Category</label>
            <select name="category" value={filters.category} onChange={handleChange}>
              <option value="">All Categories</option>
              <option value="Community">Community</option>
              <option value="Market">Market</option>
              <option value="Fitness">Fitness</option>
              <option value="Art">Art</option>
            </select>
          </div>
          
          <div>
            <label>Date</label>
            <input
              type="date"
              name="date"
              value={filters.date}
              onChange={handleChange}
            />
          </div>
          
          <div>
            <label>Location</label>
            <input
              type="text"
              name="location"
              value={filters.location}
              onChange={handleChange}
              placeholder="Search by location"
            />
          </div>
        </div>
        
        <div style={{ display: 'flex', gap: '10px' }}>
          <button onClick={handleSearch} disabled={loading}>
            {loading ? 'Searching...' : 'Apply Filter'}
          </button>
          <button onClick={handleClear} className="secondary">Clear Filter</button>
        </div>
      </div>

      {error && <div className="error">{error}</div>}

      <div>
        <h2>Search Results</h2>
        {events.length === 0 ? (
          <div className="card" style={{ padding: '40px', textAlign: 'center' }}>
            <p style={{ color: '#666', fontSize: '1.1em' }}>No events found. Try adjusting your filters.</p>
          </div>
        ) : (
          <div>
            {events.map(event => (
              <div 
                key={event.id} 
                className="card"
              >
                <h3>{event.title}</h3>
                <p><strong>Date:</strong> {event.date} | <strong>Time:</strong> {event.time} | <strong>Location:</strong> {event.location}</p>
                {event.category && <p><strong>Category:</strong> {event.category}</p>}
                {event.description && <p style={{ marginTop: '10px' }}>{event.description}</p>}
                <Link to={`/events/${event.id}`} style={{ textDecoration: 'none', display: 'inline-block', marginTop: '10px' }}>
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

