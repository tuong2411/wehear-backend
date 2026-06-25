package com.wehear.repository;

import com.wehear.model.VslTranslationCorrection;
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
public class VslTranslationCorrectionRepository {

    private final JdbcTemplate jdbcTemplate;

    public VslTranslationCorrectionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void ensureTableExists() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS vsl_translation_corrections (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                user_id BIGINT NOT NULL,
                source_text TEXT NOT NULL,
                model_name VARCHAR(64) NOT NULL,
                model_translation TEXT NOT NULL,
                corrected_translation TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                INDEX idx_vsl_correction_user_created (user_id, created_at),
                CONSTRAINT fk_vsl_correction_user FOREIGN KEY (user_id) REFERENCES users(id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        """);
    }

    private final RowMapper<VslTranslationCorrection> rowMapper = (rs, rowNum) -> {
        VslTranslationCorrection correction = new VslTranslationCorrection();
        correction.setId(rs.getLong("id"));
        correction.setUserId(rs.getLong("user_id"));
        correction.setSourceText(rs.getString("source_text"));
        correction.setModelName(rs.getString("model_name"));
        correction.setModelTranslation(rs.getString("model_translation"));
        correction.setCorrectedTranslation(rs.getString("corrected_translation"));
        try {
            correction.setUsername(rs.getString("username"));
            correction.setUserFullName(rs.getString("user_full_name"));
        } catch (Exception ignored) {
            // Some queries only select correction fields.
        }
        correction.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        correction.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return correction;
    };

    public Long save(VslTranslationCorrection correction) {
        String sql = """
            INSERT INTO vsl_translation_corrections
                (user_id, source_text, model_name, model_translation, corrected_translation)
            VALUES (?, ?, ?, ?, ?)
        """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, correction.getUserId());
            ps.setString(2, correction.getSourceText());
            ps.setString(3, correction.getModelName());
            ps.setString(4, correction.getModelTranslation());
            ps.setString(5, correction.getCorrectedTranslation());
            return ps;
        }, keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }

    public List<VslTranslationCorrection> findByUserId(Long userId) {
        String sql = """
            SELECT c.*, u.username, u.full_name AS user_full_name
            FROM vsl_translation_corrections c
            JOIN users u ON c.user_id = u.id
            WHERE c.user_id = ?
            ORDER BY c.created_at DESC
        """;
        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    public List<VslTranslationCorrection> findRecentForTraining(int limit) {
        String sql = """
            SELECT c.*, u.username, u.full_name AS user_full_name
            FROM vsl_translation_corrections c
            JOIN users u ON c.user_id = u.id
            ORDER BY c.created_at DESC
            LIMIT ?
        """;
        return jdbcTemplate.query(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setObject(1, limit, Types.INTEGER);
            return ps;
        }, rowMapper);
    }
}
