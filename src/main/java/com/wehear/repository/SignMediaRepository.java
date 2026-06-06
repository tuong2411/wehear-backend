package com.wehear.repository;

import com.wehear.model.SignMedia;
import com.wehear.repository.mapper.SignMediaRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SignMediaRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SignMediaRowMapper signMediaRowMapper;

    public SignMediaRepository(JdbcTemplate jdbcTemplate, SignMediaRowMapper signMediaRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.signMediaRowMapper = signMediaRowMapper;
    }

    public List<SignMedia> findBySignId(Long signId) {
        String sql = "SELECT * FROM sign_media WHERE sign_id = ?";
        return jdbcTemplate.query(sql, signMediaRowMapper, signId);
    }

    public List<SignMedia> findBySignIds(List<Long> signIds) {
        if (signIds == null || signIds.isEmpty()) {
            return List.of();
        }
        String inClause = String.join(",", signIds.stream().map(String::valueOf).toArray(String[]::new));
        String sql = String.format("SELECT * FROM sign_media WHERE sign_id IN (%s)", inClause);
        return jdbcTemplate.query(sql, signMediaRowMapper);
    }

    public int insert(SignMedia media) {
        String sql = "INSERT INTO sign_media(sign_id, media_type, media_url, is_primary) VALUES(?, ?, ?, ?)";
        return jdbcTemplate.update(sql, media.getSignId(), media.getMediaType(), media.getMediaUrl(), media.isPrimary() ? 1 : 0);
    }

    public int clearPrimaryBySignId(Long signId) {
        String sql = "UPDATE sign_media SET is_primary = 0 WHERE sign_id = ?";
        return jdbcTemplate.update(sql, signId);
    }

    public int deleteBySignId(Long signId) {
        String sql = "DELETE FROM sign_media WHERE sign_id = ?";
        return jdbcTemplate.update(sql, signId);
    }
    
    public int deleteById(Long id) {
        String sql = "DELETE FROM sign_media WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
