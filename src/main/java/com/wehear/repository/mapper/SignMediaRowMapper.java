package com.wehear.repository.mapper;

import com.wehear.model.SignMedia;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class SignMediaRowMapper implements RowMapper<SignMedia> {
    @Override
    public SignMedia mapRow(ResultSet rs, int rowNum) throws SQLException {
        SignMedia media = new SignMedia();
        media.setId(rs.getLong("id"));
        media.setSignId(rs.getLong("sign_id"));
        String mediaType = rs.getString("media_type");
        media.setMediaType(mediaType != null ? mediaType.toLowerCase() : null);
        media.setMediaUrl(rs.getString("media_url"));
        media.setPrimary(rs.getInt("is_primary") == 1);
        media.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        return media;
    }
}
