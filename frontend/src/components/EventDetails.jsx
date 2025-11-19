import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getEventDetails, getEventComments } from '../services/api';
import CommentSection from './CommentSection';

function EventDetails({ user }) {
  const { id } = useParams();
  const [event, setEvent] = useState(null);
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadEventDetails();
    loadComments();
  }, [id]);

  const loadEventDetails = async () => {
    try {
      setLoading(true);
      console.log('Loading event details for ID:', id);
      const response = await getEventDetails(id);
      console.log('Event details response:', response);
      console.log('Event data:', response.data);
      if (response.data && response.data.id) {
        setEvent(response.data);
        setError(null);
      } else {
        setError('Invalid event data received');
      }
    } catch (err) {
      console.error('Error loading event details:', err);
      console.error('Error response:', err.response);
      console.error('Error status:', err.response?.status);
      console.error('Error data:', err.response?.data);
      const errorMessage = err.response?.data?.error || err.message || 'Failed to load event details';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const loadComments = async () => {
    try {
      const response = await getEventComments(id);
      setComments(response.data.comments || []);
    } catch (err) {
      console.error('Failed to load comments:', err);
    }
  };

  const handleCommentUpdate = () => {
    loadComments();
  };

  if (loading) {
    return <div>Loading event details...</div>;
  }

  if (error || !event) {
    return (
      <div>
        <div className="error">{error || 'Event not found'}</div>
        <Link to="/">
          <button>Back to Events</button>
        </Link>
      </div>
    );
  }

  return (
    <div>
      <Link to="/">
        <button>Back to Events</button>
      </Link>
      
      <div style={{ 
        border: '1px solid #ddd', 
        borderRadius: '4px', 
        padding: '20px', 
        margin: '20px 0',
        backgroundColor: '#fff'
      }}>
        <h1>{event.title}</h1>
        <p><strong>Date:</strong> {event.date} | <strong>Time:</strong> {event.time} | <strong>Location:</strong> {event.location}</p>
        {event.category && <p><strong>Category:</strong> {event.category}</p>}
        {event.organizer && <p><strong>Organizer:</strong> {event.organizer}</p>}
        {event.description && <p style={{ marginTop: '15px' }}>{event.description}</p>}
      </div>

      <CommentSection 
        eventId={parseInt(id)} 
        comments={comments} 
        user={user}
        onCommentUpdate={handleCommentUpdate}
      />
    </div>
  );
}

export default EventDetails;

