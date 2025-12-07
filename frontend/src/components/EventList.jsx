import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getEvents } from '../services/api';
import FlagBanner from './FlagBanner';

function EventList() {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadEvents();
  }, []);

  const loadEvents = async () => {
    try {
      setLoading(true);
      const response = await getEvents();
      setEvents(response.data.events || []);
      setError(null);
    } catch (err) {
      setError('Failed to load events');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div>Loading events...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  return (
    <div>
      <FlagBanner />
      <h1>Community Events</h1>
      {events.length === 0 ? (
        <div style={{ padding: '20px', textAlign: 'center' }}>
          <p>No upcoming events.</p>
        </div>
      ) : (
        <div>
          {events.map(event => (
            <div 
              key={event.id} 
              className="card"
            >
              <h2>{event.title}</h2>
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
  );
}

export default EventList;

