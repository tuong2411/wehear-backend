package com.wehear.repository;

import com.wehear.model.VslUploadVideo;
import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

@Repository
public class VslUploadVideoRepository {

    private final JdbcTemplate jdbcTemplate;

    public VslUploadVideoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void ensureTableExists() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS vsl_upload_videos (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                user_id BIGINT NOT NULL,
                video_url TEXT NOT NULL,
                selected_label VARCHAR(255) NOT NULL,
                confidence DOUBLE NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                INDEX idx_vsl_upload_user_created (user_id, created_at),
                INDEX idx_vsl_upload_label (selected_label),
                CONSTRAINT fk_vsl_upload_user FOREIGN KEY (user_id) REFERENCES users(id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        """);
    }

    private final RowMapper<VslUploadVideo> rowMapper = (rs, rowNum) -> {
        VslUploadVideo item = new VslUploadVideo();
        item.setId(rs.getLong("id"));
        item.setUserId(rs.getLong("user_id"));
        item.setVideoUrl(rs.getString("video_url"));
        item.setSelectedLabel(rs.getString("selected_label"));
        double confidence = rs.getDouble("confidence");
        item.setConfidence(rs.wasNull() ? null : confidence);
        try {
            item.setUsername(rs.getString("username"));
            item.setUserFullName(rs.getString("user_full_name"));
        } catch (Exception ignored) {
            // Some queries only select upload fields.
        }
        item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        item.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return item;
    };

    public Long save(VslUploadVideo item) {
        String sql = """
            INSERT INTO vsl_upload_videos
                (user_id, video_url, selected_label, confidence)
            VALUES (?, ?, ?, ?)
        """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, item.getUserId());
            ps.setString(2, item.getVideoUrl());
            ps.setString(3, item.getSelectedLabel());
            if (item.getConfidence() == null) {
                ps.setNull(4, Types.DOUBLE);
            } else {
                ps.setDouble(4, item.getConfidence());
            }
            return ps;
        }, keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }

    public List<VslUploadVideo> findRecent(int limit) {
        String sql = """
            SELECT v.*, u.username, u.full_name AS user_full_name
            FROM vsl_upload_videos v
            JOIN users u ON v.user_id = u.id
            ORDER BY v.created_at DESC
            LIMIT ?
        """;
        return jdbcTemplate.query(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setObject(1, limit, Types.INTEGER);
            return ps;
        }, rowMapper);
    }
}
