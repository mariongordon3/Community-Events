import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { getEventDetails, getEventComments, deleteEvent } from '../services/api';
import CommentSection from './CommentSection';

function EventDetails({ user }) {
  const { id } = useParams();
  const navigate = useNavigate();
  const [event, setEvent] = useState(null);
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [deleting, setDeleting] = useState(false);

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

  const handleDeleteEvent = async () => {
    if (!window.confirm('Are you sure you want to delete this event? This action cannot be undone.')) {
      return;
    }

    try {
      setDeleting(true);
      setError(null);
      await deleteEvent(id);
      // Redirect to home page after successful deletion
      navigate('/');
    } catch (err) {
      const errorMessage = err.response?.data?.error || 'Failed to delete event';
      setError(errorMessage);
    } finally {
      setDeleting(false);
    }
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
      <div style={{ marginBottom: '20px', display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
        <Link to="/" style={{ textDecoration: 'none' }}>
          <button className="secondary">Back to Events</button>
        </Link>
        {user && event && user.userId === event.creatorId && (
          <>
            <Link to={`/events/${id}/edit`} style={{ textDecoration: 'none' }}>
              <button>Edit Event</button>
            </Link>
            <button 
              onClick={handleDeleteEvent} 
              disabled={deleting}
              className="danger"
            >
              {deleting ? 'Deleting...' : 'Delete Event'}
            </button>
          </>
        )}
      </div>

      {error && <div className="error">{error}</div>}
      
      <div className="card">
        <h1>{event.title}</h1>
        <div style={{ marginBottom: '15px', paddingBottom: '15px', borderBottom: '2px solid #e9ecef' }}>
          <p style={{ fontSize: '1.1em', marginBottom: '8px' }}>
            <strong>Date:</strong> {event.date} | <strong>Time:</strong> {event.time}
          </p>
          <p style={{ fontSize: '1.1em', marginBottom: '8px' }}>
            <strong>Location:</strong> {event.location}
          </p>
          {event.category && <p style={{ fontSize: '1.1em', marginBottom: '8px' }}><strong>Category:</strong> {event.category}</p>}
          {event.organizer && <p style={{ fontSize: '1.1em' }}><strong>Organizer:</strong> {event.organizer}</p>}
        </div>
        {event.description && <p style={{ marginTop: '15px', fontSize: '1.05em', lineHeight: '1.8' }}>{event.description}</p>}
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

