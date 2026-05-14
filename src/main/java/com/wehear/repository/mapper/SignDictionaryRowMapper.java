package com.wehear.repository.mapper;

import com.wehear.model.SignDictionary;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class SignDictionaryRowMapper implements RowMapper<SignDictionary> {
    @Override
    public SignDictionary mapRow(ResultSet rs, int rowNum) throws SQLException {
        SignDictionary sign = new SignDictionary();
        sign.setId(rs.getLong("id"));
        sign.setLabelCode(rs.getString("label_code"));
        sign.setSignWord(rs.getString("sign_word"));
        sign.setDescription(rs.getString("description"));
        sign.setRegion(rs.getString("region"));
        sign.setDifficultyLevel(rs.getString("difficulty_level"));
        sign.setExampleSentence(rs.getString("example_sentence"));
        sign.setActive(rs.getInt("is_active") == 1);
        sign.setCreatedBy(rs.getLong("created_by"));
        sign.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        sign.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
        return sign;
    }
}
