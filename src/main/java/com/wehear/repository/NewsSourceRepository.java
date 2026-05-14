package com.wehear.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.wehear.model.NewsSource;
import com.wehear.repository.mapper.NewsSourceRowMapper;

@Repository
public class NewsSourceRepository {

	private final JdbcTemplate jdbcTemplate;
    private final NewsSourceRowMapper rowMapper;

    public NewsSourceRepository(JdbcTemplate jdbcTemplate, NewsSourceRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }
    
    public List<NewsSource> findAllActive() {
        String sql = """
                SELECT *
                FROM news_sources
                WHERE is_active = 1
                ORDER BY id ASC
                """;
        return jdbcTemplate.query(sql, rowMapper);
    }

    public NewsSource findById(Long id) {
        String sql = "SELECT * FROM news_sources WHERE id = ? LIMIT 1";
        List<NewsSource> result = jdbcTemplate.query(sql, rowMapper, id);
        return result.isEmpty() ? null : result.get(0);
    }
}
