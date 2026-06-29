package com.wehear.repository;

import com.wehear.model.SignDictionary;
import com.wehear.repository.mapper.SignDictionaryRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class SignDictionaryRepository {

    private static final Logger logger = LoggerFactory.getLogger(SignDictionaryRepository.class);
    private final JdbcTemplate jdbcTemplate;
    private final SignDictionaryRowMapper signDictionaryRowMapper;

    public SignDictionaryRepository(JdbcTemplate jdbcTemplate, SignDictionaryRowMapper signDictionaryRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.signDictionaryRowMapper = signDictionaryRowMapper;
    }

    public List<SignDictionary> findAll() {
        String sql = "SELECT * FROM sign_dictionary ORDER BY id DESC";
        return jdbcTemplate.query(sql, signDictionaryRowMapper);
    }

    public List<SignDictionary> findAllPaginated(int offset, int limit, String search, String region) {
        StringBuilder sql = new StringBuilder("SELECT * FROM sign_dictionary WHERE 1=1");
        List<Object> params = new java.util.ArrayList<>();

        if (search != null && !search.isEmpty()) {
            sql.append(" AND (sign_word LIKE CONCAT('%', ?, '%') OR label_code LIKE CONCAT('%', ?, '%'))");
            params.add(search);
            params.add(search);
        }

        appendRegionFilter(sql, params, region);

        sql.append(" ORDER BY sign_word ASC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        logger.info("Executing SQL: {} with params: {}", sql, params);
        return jdbcTemplate.query(sql.toString(), signDictionaryRowMapper, params.toArray());
    }

    public List<SignDictionary> findBySignWordExact(String word) {
        String searchWord = word.trim().toLowerCase();
        
        String exactSql = "SELECT * FROM sign_dictionary WHERE BINARY LOWER(sign_word) = LOWER(?) AND is_active = 1";
        return jdbcTemplate.query(exactSql, signDictionaryRowMapper, searchWord);
    }

    public int countTotal(String search, String region) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM sign_dictionary WHERE 1=1");
        List<Object> params = new java.util.ArrayList<>();

        if (search != null && !search.isEmpty()) {
            sql.append(" AND (sign_word LIKE CONCAT('%', ?, '%') OR label_code LIKE CONCAT('%', ?, '%'))");
            params.add(search);
            params.add(search);
        }

        appendRegionFilter(sql, params, region);

        logger.info("Executing COUNT SQL: {} with params: {}", sql, params);
        Integer count = jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
        return count != null ? count : 0;
    }

    public SignDictionary findById(Long id) {
        String sql = "SELECT * FROM sign_dictionary WHERE id = ?";
        List<SignDictionary> results = jdbcTemplate.query(sql, signDictionaryRowMapper, id);
        return results.isEmpty() ? null : results.get(0);
    }

    public SignDictionary findByLabelCode(String labelCode) {
        String sql = "SELECT * FROM sign_dictionary WHERE label_code = ?";
        List<SignDictionary> results = jdbcTemplate.query(sql, signDictionaryRowMapper, labelCode);
        return results.isEmpty() ? null : results.get(0);
    }

    public Long insert(SignDictionary sign) {
        String sql = "INSERT INTO sign_dictionary(label_code, sign_word, description, region, difficulty_level, example_sentence, is_active, created_by) " +
                     "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, sign.getLabelCode());
            ps.setString(2, sign.getSignWord());
            ps.setString(3, sign.getDescription());
            ps.setString(4, sign.getRegion());
            ps.setString(5, sign.getDifficultyLevel());
            ps.setString(6, sign.getExampleSentence());
            ps.setInt(7, sign.isActive() ? 1 : 0);
            if (sign.getCreatedBy() != null) {
                ps.setLong(8, sign.getCreatedBy());
            } else {
                ps.setNull(8, java.sql.Types.BIGINT);
            }
            return ps;
        }, keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }

    public int update(SignDictionary sign) {
        String sql = "UPDATE sign_dictionary SET label_code = ?, sign_word = ?, description = ?, region = ?, " +
                     "difficulty_level = ?, example_sentence = ?, is_active = ? WHERE id = ?";
        return jdbcTemplate.update(sql, sign.getLabelCode(), sign.getSignWord(), sign.getDescription(),
                sign.getRegion(), sign.getDifficultyLevel(), sign.getExampleSentence(),
                sign.isActive() ? 1 : 0, sign.getId());
    }

    public int updateStatus(Long id, boolean active) {
        String sql = "UPDATE sign_dictionary SET is_active = ? WHERE id = ?";
        return jdbcTemplate.update(sql, active ? 1 : 0, id);
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM sign_dictionary WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public void bulkDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        String sql = "DELETE FROM sign_dictionary WHERE id IN (" + 
                     String.join(",", java.util.Collections.nCopies(ids.size(), "?")) + ")";
        jdbcTemplate.update(sql, ids.toArray());
    }

    private void appendRegionFilter(StringBuilder sql, List<Object> params, String region) {
        if (region == null || region.equals("all")) {
            return;
        }

        sql.append(" AND (region LIKE CONCAT('%', ?, '%') OR region LIKE '%Toàn quốc%')");
        params.add(region);
    }
}
