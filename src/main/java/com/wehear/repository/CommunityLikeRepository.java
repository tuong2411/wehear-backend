package com.wehear.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CommunityLikeRepository {

    private final JdbcTemplate jdbcTemplate;

    public CommunityLikeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean togglePostLike(Long userId, Long postId) {
        String checkSql = "SELECT COUNT(*) FROM community_likes WHERE user_id = ? AND post_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, userId, postId);
        
        if (count != null && count > 0) {
            String deleteSql = "DELETE FROM community_likes WHERE user_id = ? AND post_id = ?";
            jdbcTemplate.update(deleteSql, userId, postId);
            return false; // Unliked
        } else {
            String insertSql = "INSERT INTO community_likes (user_id, post_id) VALUES (?, ?)";
            jdbcTemplate.update(insertSql, userId, postId);
            return true; // Liked
        }
    }

    public boolean toggleCommentLike(Long userId, Long commentId) {
        String checkSql = "SELECT COUNT(*) FROM community_likes WHERE user_id = ? AND comment_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, userId, commentId);
        
        if (count != null && count > 0) {
            String deleteSql = "DELETE FROM community_likes WHERE user_id = ? AND comment_id = ?";
            jdbcTemplate.update(deleteSql, userId, commentId);
            return false; // Unliked
        } else {
            String insertSql = "INSERT INTO community_likes (user_id, comment_id) VALUES (?, ?)";
            jdbcTemplate.update(insertSql, userId, commentId);
            return true; // Liked
        }
    }
}
