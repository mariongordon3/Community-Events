import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { addComment, editComment, deleteComment } from '../services/api';

function CommentSection({ eventId, comments, user, onCommentUpdate }) {
  const [newComment, setNewComment] = useState('');
  const [editingCommentId, setEditingCommentId] = useState(null);
  const [editText, setEditText] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleAddComment = async () => {
    if (!user) {
      navigate('/login');
      return;
    }

    if (!newComment.trim()) {
      setError('Comment text is required');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      await addComment(eventId, newComment);
      setNewComment('');
      onCommentUpdate();
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to add comment');
    } finally {
      setLoading(false);
    }
  };

  const handleStartEdit = (comment) => {
    setEditingCommentId(comment.id);
    setEditText(comment.text);
    setError(null);
  };

  const handleSaveEdit = async (commentId) => {
    if (!editText.trim()) {
      setError('Comment text is required');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      await editComment(commentId, editText);
      setEditingCommentId(null);
      setEditText('');
      onCommentUpdate();
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to edit comment');
    } finally {
      setLoading(false);
    }
  };

  const handleCancelEdit = () => {
    setEditingCommentId(null);
    setEditText('');
    setError(null);
  };

  const handleDelete = async (commentId) => {
    if (!window.confirm('Are you sure you want to delete this comment?')) {
      return;
    }

    try {
      setLoading(true);
      setError(null);
      await deleteComment(commentId);
      onCommentUpdate();
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to delete comment');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card">
      <h2>Comments</h2>
      
      {error && <div className="error">{error}</div>}

      <div style={{ marginBottom: '25px' }}>
        {comments.length === 0 ? (
          <p style={{ color: '#666', fontStyle: 'italic', padding: '15px', textAlign: 'center' }}>
            No comments yet. Be the first to comment!
          </p>
        ) : (
          comments.map(comment => (
            <div key={comment.id} className="card" style={{ 
              marginBottom: '15px',
              padding: '15px',
              backgroundColor: '#f8f9fa'
            }}>
              {editingCommentId === comment.id ? (
                <div>
                  <textarea
                    value={editText}
                    onChange={(e) => setEditText(e.target.value)}
                    style={{ width: '100%', minHeight: '80px', marginBottom: '10px' }}
                    placeholder="Edit your comment..."
                  />
                  <div style={{ display: 'flex', gap: '10px' }}>
                    <button onClick={() => handleSaveEdit(comment.id)} disabled={loading}>
                      Save
                    </button>
                    <button onClick={handleCancelEdit} disabled={loading} className="secondary">
                      Cancel
                    </button>
                  </div>
                </div>
              ) : (
                <div>
                  <p style={{ marginBottom: '8px', fontSize: '1.05em' }}>
                    <strong style={{ color: '#007bff' }}>{comment.userName || 'User'}:</strong>
                  </p>
                  <p style={{ marginBottom: '12px', color: '#333', lineHeight: '1.6' }}>{comment.text}</p>
                  {user && user.userId === comment.userId && (
                    <div style={{ display: 'flex', gap: '10px' }}>
                      <button 
                        onClick={() => handleStartEdit(comment)} 
                        disabled={loading}
                        style={{ padding: '6px 12px', fontSize: '13px' }}
                      >
                        Edit
                      </button>
                      <button 
                        onClick={() => handleDelete(comment.id)} 
                        disabled={loading}
                        className="danger"
                        style={{ padding: '6px 12px', fontSize: '13px' }}
                      >
                        Delete
                      </button>
                    </div>
                  )}
                </div>
              )}
            </div>
          ))
        )}
      </div>

      <div style={{ borderTop: '2px solid #e9ecef', paddingTop: '20px' }}>
        <h3>Add a Comment</h3>
        {!user ? (
          <p style={{ color: '#666', marginTop: '10px' }}>
            Please <a href="/login" style={{ color: '#007bff', textDecoration: 'none', fontWeight: '500' }}>log in</a> to add a comment.
          </p>
        ) : (
          <div>
            <textarea
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              placeholder="Your comment..."
              style={{ width: '100%', minHeight: '80px', marginBottom: '10px' }}
            />
            <button onClick={handleAddComment} disabled={loading || !newComment.trim()}>
              {loading ? 'Posting...' : 'Post Comment'}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

export default CommentSection;

