package com.Main.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.Main.entity.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("UserId"));
        user.setEmail(rs.getString("UserEmail"));
        user.setPassword(rs.getString("UserPwd"));
        user.setName(rs.getString("UserName"));
        user.setLocation(rs.getString("Location"));
        user.setAvatarUrl(rs.getString("avatarUrl"));
        user.setMember(rs.getBoolean("Member"));
        user.setCreatedAt(rs.getTimestamp("CreateTime").getTime());
        return user;
    }
}
