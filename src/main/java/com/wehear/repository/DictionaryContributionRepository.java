package com.wehear.repository;

import com.wehear.model.DictionaryContribution;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class DictionaryContributionRepository {

    private final JdbcTemplate jdbcTemplate;

    public DictionaryContributionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<DictionaryContribution> rowMapper = (rs, rowNum) -> {
        DictionaryContribution contribution = new DictionaryContribution();
        contribution.setId(rs.getLong("id"));
        contribution.setUserId(rs.getLong("user_id"));
        contribution.setWord(rs.getString("word"));
        contribution.setDescription(rs.getString("description"));
        contribution.setExample(rs.getString("example"));
        contribution.setVideoUrl(rs.getString("video_url"));
        contribution.setType(rs.getString("type"));
        contribution.setTargetDictionaryId(rs.getObject("target_dictionary_id", Long.class));
        contribution.setStatus(rs.getString("status"));
        contribution.setAdminNote(rs.getString("admin_note"));
        contribution.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        contribution.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return contribution;
    };

    public Long save(DictionaryContribution contribution) {
        String sql = "INSERT INTO dictionary_contributions (user_id, word, description, example, video_url, type, target_dictionary_id, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, contribution.getUserId());
            ps.setString(2, contribution.getWord());
            ps.setString(3, contribution.getDescription());
            ps.setString(4, contribution.getExample());
            ps.setString(5, contribution.getVideoUrl());
            ps.setString(6, contribution.getType());
            if (contribution.getTargetDictionaryId() != null) {
                ps.setLong(7, contribution.getTargetDictionaryId());
            } else {
                ps.setNull(7, java.sql.Types.BIGINT);
            }
            ps.setString(8, contribution.getStatus() != null ? contribution.getStatus() : "PENDING");
            return ps;
        }, keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }

    public Optional<DictionaryContribution> findById(Long id) {
        String sql = "SELECT * FROM dictionary_contributions WHERE id = ?";
        List<DictionaryContribution> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.stream().findFirst();
    }

    public List<DictionaryContribution> findByUserId(Long userId) {
        String sql = "SELECT * FROM dictionary_contributions WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    public List<DictionaryContribution> findByStatus(String status) {
        String sql = "SELECT * FROM dictionary_contributions WHERE status = ? ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, rowMapper, status);
    }

    public int updateStatus(Long id, String status, String adminNote) {
        String sql = "UPDATE dictionary_contributions SET status = ?, admin_note = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        return jdbcTemplate.update(sql, status, adminNote, id);
    }

    public int clearTargetDictionaryId(Long dictionaryId) {
        String sql = "UPDATE dictionary_contributions SET target_dictionary_id = NULL WHERE target_dictionary_id = ?";
        return jdbcTemplate.update(sql, dictionaryId);
    }

    public int clearTargetDictionaryIds(List<Long> dictionaryIds) {
        if (dictionaryIds == null || dictionaryIds.isEmpty()) {
            return 0;
        }
        String placeholders = String.join(",", java.util.Collections.nCopies(dictionaryIds.size(), "?"));
        String sql = "UPDATE dictionary_contributions SET target_dictionary_id = NULL WHERE target_dictionary_id IN (" + placeholders + ")";
        return jdbcTemplate.update(sql, dictionaryIds.toArray());
    }
}
