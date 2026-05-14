package com.wehear.repository;

import com.wehear.model.CommunityComment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommunityCommentRepository {

    private final JdbcTemplate jdbcTemplate;

    public CommunityCommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<CommunityComment> rowMapper = (rs, rowNum) -> {
        CommunityComment comment = new CommunityComment();
        comment.setId(rs.getLong("id"));
        comment.setPostId(rs.getLong("post_id"));
        comment.setUserId(rs.getLong("user_id"));
        comment.setContent(rs.getString("content"));
        comment.setParentId(rs.getObject("parent_id", Long.class));
        comment.setStatus(rs.getString("status"));
        comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        // Bonus fields
        comment.setUserFullName(rs.getString("full_name"));
        comment.setUserAvatarUrl(rs.getString("avatar_url"));
        comment.setLikeCount(rs.getInt("like_count"));
        return comment;
    };

    public void save(CommunityComment comment) {
        String sql = "INSERT INTO community_comments (post_id, user_id, content, parent_id, status) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, 
                comment.getPostId(), 
                comment.getUserId(), 
                comment.getContent(), 
                comment.getParentId(), 
                comment.getStatus() != null ? comment.getStatus() : "ACTIVE");
    }

    public List<CommunityComment> findByPostId(Long postId) {
        String sql = "SELECT c.*, u.full_name, u.avatar_url, " +
                     "(SELECT COUNT(*) FROM community_likes WHERE comment_id = c.id) as like_count " +
                     "FROM community_comments c " +
                     "JOIN users u ON c.user_id = u.id " +
                     "WHERE c.post_id = ? AND c.status = 'ACTIVE' " +
                     "ORDER BY c.created_at ASC";
        return jdbcTemplate.query(sql, rowMapper, postId);
    }

    public void updateStatus(Long id, String status) {
        String sql = "UPDATE community_comments SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, id);
    }

    public void hideCommentsByPostId(Long postId) {
        String sql = "UPDATE community_comments SET status = 'HIDDEN' WHERE post_id = ?";
        jdbcTemplate.update(sql, postId);
    }

    public boolean isLikedByUser(Long commentId, Long userId) {
        String sql = "SELECT COUNT(*) FROM community_likes WHERE comment_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, commentId, userId);
        return count != null && count > 0;
    }
}
