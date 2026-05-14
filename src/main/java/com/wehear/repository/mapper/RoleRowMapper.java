package com.wehear.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.wehear.model.Role;

@Component
public class RoleRowMapper implements RowMapper<Role> {

	@Override
	public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new Role(
				rs.getLong("id"),
				rs.getString("name"),
				rs.getString("description")
				);
	}

}
