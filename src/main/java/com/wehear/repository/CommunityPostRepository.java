package com.wehear.repository;

import com.wehear.model.CommunityPost;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class CommunityPostRepository {

    private final JdbcTemplate jdbcTemplate;

    public CommunityPostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<CommunityPost> rowMapper = (rs, rowNum) -> {
        CommunityPost post = new CommunityPost();
        post.setId(rs.getLong("id"));
        post.setUserId(rs.getLong("user_id"));
        post.setTitle(rs.getString("title"));
        post.setContent(rs.getString("content"));
        post.setMediaUrl(rs.getString("media_url"));
        post.setMediaType(rs.getString("media_type"));
        post.setStatus(rs.getString("status"));
        post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        post.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        
        // Bonus fields from JOIN
        post.setUserFullName(rs.getString("full_name"));
        post.setUserAvatarUrl(rs.getString("avatar_url"));
        post.setLikeCount(rs.getInt("like_count"));
        post.setCommentCount(rs.getInt("comment_count"));
        return post;
    };

    public Long save(CommunityPost post) {
        String sql = "INSERT INTO community_posts (user_id, title, content, media_url, media_type, status) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, post.getUserId());
            ps.setString(2, post.getTitle());
            ps.setString(3, post.getContent());
            ps.setString(4, post.getMediaUrl());
            ps.setString(5, post.getMediaType());
            ps.setString(6, post.getStatus() != null ? post.getStatus() : "ACTIVE");
            return ps;
        }, keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }

    public List<CommunityPost> findAllActive(int limit, int offset, Long currentUserId) {
        String sql = "SELECT p.*, u.full_name, u.avatar_url, " +
                     "(SELECT COUNT(*) FROM community_likes WHERE post_id = p.id) as like_count, " +
                     "(SELECT COUNT(*) FROM community_comments WHERE post_id = p.id AND status = 'ACTIVE') as comment_count " +
                     "FROM community_posts p " +
                     "JOIN users u ON p.user_id = u.id " +
                     "WHERE p.status = 'ACTIVE' " +
                     "ORDER BY p.created_at DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, rowMapper, limit, offset);
    }

    public List<CommunityPost> findAll(int limit, int offset) {
        String sql = "SELECT p.*, u.full_name, u.avatar_url, " +
                     "(SELECT COUNT(*) FROM community_likes WHERE post_id = p.id) as like_count, " +
                     "(SELECT COUNT(*) FROM community_comments WHERE post_id = p.id AND status = 'ACTIVE') as comment_count " +
                     "FROM community_posts p " +
                     "JOIN users u ON p.user_id = u.id " +
                     "ORDER BY p.created_at DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, rowMapper, limit, offset);
    }

    public Optional<CommunityPost> findById(Long id) {
        String sql = "SELECT p.*, u.full_name, u.avatar_url, " +
                     "(SELECT COUNT(*) FROM community_likes WHERE post_id = p.id) as like_count, " +
                     "(SELECT COUNT(*) FROM community_comments WHERE post_id = p.id AND status = 'ACTIVE') as comment_count " +
                     "FROM community_posts p " +
                     "JOIN users u ON p.user_id = u.id " +
                     "WHERE p.id = ?";
        List<CommunityPost> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.stream().findFirst();
    }

    public void updateStatus(Long id, String status) {
        String sql = "UPDATE community_posts SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, id);
    }

    public boolean isLikedByUser(Long postId, Long userId) {
        String sql = "SELECT COUNT(*) FROM community_likes WHERE post_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, postId, userId);
        return count != null && count > 0;
    }
}
