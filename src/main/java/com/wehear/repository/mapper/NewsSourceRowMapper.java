package com.wehear.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.wehear.model.NewsSource;

@Component
public class NewsSourceRowMapper implements RowMapper<NewsSource> {

	@Override
	public NewsSource mapRow(ResultSet rs, int rowNum) throws SQLException {
		NewsSource source = new NewsSource();
        source.setId(rs.getLong("id"));
        source.setSourceName(rs.getString("source_name"));
        source.setSourceType(rs.getString("source_type"));
        source.setBaseUrl(rs.getString("base_url"));
        source.setRssUrl(rs.getString("rss_url"));
        source.setApiUrl(rs.getString("api_url"));
        source.setActive(rs.getInt("is_active") == 1);
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            source.setCreatedAt(createdAt.toLocalDateTime());
        }
        return source;
	}
}
