package com.wehear.repository.mapper;

import com.wehear.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = User.builder()
                .id(rs.getLong("id"))
                .username(rs.getString("username"))
                .password(rs.getString("password"))
                .email(rs.getString("email"))
                .fullName(rs.getString("full_name"))
                .phoneNumber(rs.getString("phone_number"))
                .avatarUrl(rs.getString("avatar_url"))
                .roleId(rs.getLong("role_id"))
                .status(rs.getInt("status"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
        
        try {
            user.setRoleName(rs.getString("role_name"));
        } catch (SQLException ignored) {}
        
        return user;
    }
}
