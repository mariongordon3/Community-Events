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
    <div style={{ 
      border: '1px solid #ddd', 
      borderRadius: '4px', 
      padding: '20px', 
      margin: '20px 0',
      backgroundColor: '#fff'
    }}>
      <h2>Comments</h2>
      
      {error && <div className="error">{error}</div>}

      <div style={{ marginBottom: '20px' }}>
        {comments.length === 0 ? (
          <p>No comments yet. Be the first to comment!</p>
        ) : (
          comments.map(comment => (
            <div key={comment.id} style={{ 
              borderBottom: '1px solid #eee', 
              padding: '10px 0',
              marginBottom: '10px'
            }}>
              {editingCommentId === comment.id ? (
                <div>
                  <textarea
                    value={editText}
                    onChange={(e) => setEditText(e.target.value)}
                    style={{ width: '100%', minHeight: '60px', marginBottom: '10px' }}
                  />
                  <button onClick={() => handleSaveEdit(comment.id)} disabled={loading}>
                    Save
                  </button>
                  <button onClick={handleCancelEdit} disabled={loading}>
                    Cancel
                  </button>
                </div>
              ) : (
                <div>
                  <p><strong>{comment.userName || 'User'}:</strong> {comment.text}</p>
                  {user && user.userId === comment.userId && (
                    <div>
                      <button onClick={() => handleStartEdit(comment)} disabled={loading}>
                        Edit
                      </button>
                      <button onClick={() => handleDelete(comment.id)} disabled={loading}>
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

      <div>
        <h3>Add a Comment</h3>
        {!user ? (
          <p>Please <a href="/login">log in</a> to add a comment.</p>
        ) : (
          <div>
            <textarea
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              placeholder="Your comment..."
              style={{ width: '100%', minHeight: '60px', marginBottom: '10px' }}
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

