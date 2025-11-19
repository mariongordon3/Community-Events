import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { createEvent, updateEvent, getEventDetails } from '../services/api';

function EventForm({ user }) {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEditMode = !!id;
  const [formData, setFormData] = useState({
    title: '',
    date: '',
    time: '',
    location: '',
    description: '',
    category: ''
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [loadingEvent, setLoadingEvent] = useState(isEditMode);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    if (isEditMode) {
      loadEventData();
    }
  }, [id]);

  const loadEventData = async () => {
    try {
      setLoadingEvent(true);
      const response = await getEventDetails(id);
      const event = response.data;
      setFormData({
        title: event.title || '',
        date: event.date || '',
        time: event.time || '',
        location: event.location || '',
        description: event.description || '',
        category: event.category || ''
      });
    } catch (err) {
      setError('Failed to load event data');
      console.error(err);
    } finally {
      setLoadingEvent(false);
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    // Clear error for this field
    if (errors[e.target.name]) {
      setErrors({
        ...errors,
        [e.target.name]: ''
      });
    }
  };

  const validate = () => {
    const newErrors = {};
    
    if (!formData.title.trim()) {
      newErrors.title = 'Event title is required';
    }
    if (!formData.date.trim()) {
      newErrors.date = 'Event date is required';
    }
    if (!formData.time.trim()) {
      newErrors.time = 'Event time is required';
    }
    if (!formData.location.trim()) {
      newErrors.location = 'Event location is required';
    }
    if (!formData.description.trim()) {
      newErrors.description = 'Event description is required';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validate()) {
      return;
    }

    try {
      setLoading(true);
      setError(null);
      setSuccess(false);
      
      let response;
      if (isEditMode) {
        response = await updateEvent(id, formData);
        setSuccess(true);
        // Redirect to event details after a short delay
        setTimeout(() => {
          navigate(`/events/${id}`);
        }, 1500);
      } else {
        response = await createEvent(formData);
        setSuccess(true);
        // Redirect to event details after a short delay
        setTimeout(() => {
          navigate(`/events/${response.data.id}`);
        }, 1500);
      }
    } catch (err) {
      const errorMessage = err.response?.data?.error || (isEditMode ? 'Failed to update event' : 'Failed to create event');
      setError(errorMessage);
      
      // If it's a field validation error, set it in errors
      if (err.response?.status === 400) {
        const fieldErrors = {};
        const errorText = errorMessage.toLowerCase();
        if (errorText.includes('title')) fieldErrors.title = errorMessage;
        if (errorText.includes('date')) fieldErrors.date = errorMessage;
        if (errorText.includes('time')) fieldErrors.time = errorMessage;
        if (errorText.includes('location')) fieldErrors.location = errorMessage;
        if (errorText.includes('description')) fieldErrors.description = errorMessage;
        setErrors(fieldErrors);
      }
    } finally {
      setLoading(false);
    }
  };

  if (loadingEvent) {
    return <div>Loading event data...</div>;
  }

  return (
    <div>
      <h1>{isEditMode ? 'Edit Event' : 'Create New Event'}</h1>
      
      {success && (
        <div className="success">
          {isEditMode ? 'Event updated successfully! Redirecting...' : 'Event created successfully! Redirecting...'}
        </div>
      )}
      
      {error && <div className="error">{error}</div>}

      <form onSubmit={handleSubmit} style={{ 
        maxWidth: '600px', 
        padding: '20px', 
        backgroundColor: '#fff', 
        borderRadius: '4px',
        border: '1px solid #ddd'
      }}>
        <div style={{ marginBottom: '15px' }}>
          <label>Event Title: *</label>
          <input
            type="text"
            name="title"
            value={formData.title}
            onChange={handleChange}
            style={{ width: '100%' }}
          />
          {errors.title && <div className="error" style={{ marginTop: '5px', fontSize: '12px' }}>{errors.title}</div>}
        </div>

        <div style={{ marginBottom: '15px' }}>
          <label>Date: *</label>
          <input
            type="date"
            name="date"
            value={formData.date}
            onChange={handleChange}
            style={{ width: '100%' }}
          />
          {errors.date && <div className="error" style={{ marginTop: '5px', fontSize: '12px' }}>{errors.date}</div>}
        </div>

        <div style={{ marginBottom: '15px' }}>
          <label>Time: *</label>
          <input
            type="time"
            name="time"
            value={formData.time}
            onChange={handleChange}
            style={{ width: '100%' }}
          />
          {errors.time && <div className="error" style={{ marginTop: '5px', fontSize: '12px' }}>{errors.time}</div>}
        </div>

        <div style={{ marginBottom: '15px' }}>
          <label>Location: *</label>
          <input
            type="text"
            name="location"
            value={formData.location}
            onChange={handleChange}
            style={{ width: '100%' }}
          />
          {errors.location && <div className="error" style={{ marginTop: '5px', fontSize: '12px' }}>{errors.location}</div>}
        </div>

        <div style={{ marginBottom: '15px' }}>
          <label>Category:</label>
          <select
            name="category"
            value={formData.category}
            onChange={handleChange}
            style={{ width: '100%' }}
          >
            <option value="">Select Category</option>
            <option value="Community">Community</option>
            <option value="Market">Market</option>
            <option value="Fitness">Fitness</option>
            <option value="Art">Art</option>
          </select>
        </div>

        <div style={{ marginBottom: '15px' }}>
          <label>Description: *</label>
          <textarea
            name="description"
            value={formData.description}
            onChange={handleChange}
            style={{ width: '100%', minHeight: '100px' }}
          />
          {errors.description && <div className="error" style={{ marginTop: '5px', fontSize: '12px' }}>{errors.description}</div>}
        </div>

        <div>
          <button type="submit" disabled={loading}>
            {loading ? (isEditMode ? 'Updating...' : 'Creating...') : (isEditMode ? 'Update Event' : 'Save Event')}
          </button>
          <button type="button" onClick={() => isEditMode ? navigate(`/events/${id}`) : navigate('/')} disabled={loading}>
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
}

export default EventForm;

