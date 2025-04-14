package com.Main.service;

import com.Main.RowMapper.UserRowMapper;
import com.Main.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Component
public class UserService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    JdbcTemplate jdbcTemplate;

    RowMapper<User> userRowMapper = new BeanPropertyRowMapper<>(User.class);

    public List<User> getUsers(){
        return jdbcTemplate.query("select * from User", new UserRowMapper());
    }

    public User getUserById(long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM User WHERE UserId = ?", new Object[] { id }, new UserRowMapper());
    }

    public User getUserByEmail(String email) {
        return jdbcTemplate.queryForObject("SELECT * FROM User WHERE UserEmail = ?", new Object[]{email}, new UserRowMapper());
    }

    public User signin(String email, String password) {
        logger.info("try login by {}...", email);
        User user = getUserByEmail(email);
        System.out.println("User found: " + user);
        if (user.getPassword().equals(password)) {
            System.out.println("Password is correct");
            return user;
        }
        throw new RuntimeException("login failed.");
    }

    public User register(String email, String password, String name) {
        logger.info("try register by {}...", email);
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setCreatedAt(System.currentTimeMillis());
        KeyHolder holder = new GeneratedKeyHolder();
        try {
            if (1 != jdbcTemplate.update((conn) -> {
                var ps = conn.prepareStatement("INSERT INTO User(UserEmail, UserPwd, UserName, CreateTime) VALUES(?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, user.getEmail());
                ps.setObject(2, user.getPassword());
                ps.setObject(3, user.getName());
                ps.setObject(4, new Timestamp(user.getCreatedAt()));  // 转换为 Timestamp
                return ps;
            }, holder)) {
                throw new RuntimeException("Insert failed.");
            }
        } catch (DataAccessException e) {
            logger.error("SQL Error: " + e.getMessage(), e);
            throw new RuntimeException("Insert failed due to SQL error.", e);
        }

        user.setId(holder.getKey().intValue());
        return user;
    }

    public void updateUser(User user) {
        if (1 != jdbcTemplate.update("UPDATE User SET UserName = ? WHERE UserId=?", user.getName(), user.getId())) {
            throw new RuntimeException("User not found by id");
        }
    }
    public User ChangeUserPwd(String newpassword, String email) {
        // 增强日志，记录尝试修改密码的详细信息
        logger.info("Attempting to change password for user with email: {}", email);

        // SQL更新语句
        String sql = "UPDATE User SET UserPwd = ? WHERE UserEmail = ?";

        // 执行更新，rowsAffected表示受影响的行数
        int rowsAffected = jdbcTemplate.update(sql, newpassword, email);

        // 增强日志，记录是否更新成功
        if (rowsAffected > 0) {
            logger.info("Password successfully changed for user with email: {}", email);
        } else {
            logger.error("Failed to update password for user with email: {}", email);
            throw new RuntimeException("Failed to update the password for user with email: " + email);
        }

        // 返回更新后的用户信息
        // 这里假设你有一个方法能够根据email从数据库中重新加载用户信息queryForObjectl = ?";
        String query = "SELECT * FROM User WHERE UserEmail = ?";
        User updatedUser = jdbcTemplate.queryForObject(query, new Object[] { email }, new UserRowMapper()); // 假设UserRowMapper是一个适当的RowMapper
        return updatedUser;
    }
    public User ChangeUserName(String newName, String email) {
        // 增强日志，记录尝试修改密码的详细信息
        logger.info("Attempting to change password for user with email: {}", email);

        // SQL更新语句
        String sql = "UPDATE User SET UserName = ? WHERE UserEmail = ?";

        // 执行更新，rowsAffected表示受影响的行数
        int rowsAffected = jdbcTemplate.update(sql, newName, email);

        // 增强日志，记录是否更新成功
        if (rowsAffected > 0) {
            logger.info("Name successfully changed for user with email: {}", email);
        } else {
            logger.error("Failed to update name for user with email: {}", email);
            throw new RuntimeException("Failed to update the password for user with email: " + email);
        }

        // 返回更新后的用户信息
        // 这里假设你有一个方法能够根据email从数据库中重新加载用户信息queryForObjectl = ?";
        String query = "SELECT * FROM User WHERE UserEmail = ?";
        User updatedUser = jdbcTemplate.queryForObject(query, new Object[] { email }, new UserRowMapper()); // 假设UserRowMapper是一个适当的RowMapper
        return updatedUser;
    }
    public User ChangeUserEmail(String newEmail, String email) {
        // 增强日志，记录尝试修改密码的详细信息
        logger.info("Attempting to change email for user with email: {}", email);

        // SQL更新语句
        String sql = "UPDATE User SET UserEmail = ? WHERE UserEmail = ?";

        // 执行更新，rowsAffected表示受影响的行数
        int rowsAffected = jdbcTemplate.update(sql, newEmail, email);

        // 增强日志，记录是否更新成功
        if (rowsAffected > 0) {
            logger.info("Email successfully changed for user with email: {}", email);
        } else {
            logger.error("Failed to update email for user with email: {}", email);
            throw new RuntimeException("Failed to update the password for user with email: " + email);
        }

        // 返回更新后的用户信息
        // 这里假设你有一个方法能够根据email从数据库中重新加载用户信息queryForObjectl = ?";
        String query = "SELECT * FROM User WHERE UserEmail = ?";
        User updatedUser = jdbcTemplate.queryForObject(query, new Object[] { email }, new UserRowMapper()); // 假设UserRowMapper是一个适当的RowMapper
        return updatedUser;
    }

    public User ChangeUserAvatar(String avatarUrl, String email) {
        logger.info("Attempting to change avatarURL for user with email: {}", email);
        String sql = "UPDATE User SET avatarUrl = ? WHERE UserEmail = ?";
        int rowsAffected = jdbcTemplate.update(sql, avatarUrl, email);
        if (rowsAffected > 0) {
            logger.info("Avatar successfully changed for user with email: {}", email);
        } else {
            logger.error("Failed to update avatarURL for user with email: {}", email);
            throw new RuntimeException("Failed to update the avatarURL for user with email: " + email);
        }
        String query = "SELECT * FROM User WHERE UserEmail = ?";
        User updatedUser = jdbcTemplate.queryForObject(query, new Object[] { email }, new UserRowMapper()); // 假设UserRowMapper是一个适当的RowMapper
        return updatedUser;
    }

    public User ChangeUserLocation(String address, String email) {
        logger.info("Attempting to change Address for user with email: {}", email);
        String sql = "UPDATE User SET Location = ? WHERE UserEmail = ?";
        int rowsAffected = jdbcTemplate.update(sql, address, email);
        if (rowsAffected > 0) {
            logger.info("Address successfully changed for user with email: {}", email);
        }else{
            logger.error("Failed to update address for user with email: {}", email);
            throw new RuntimeException("Failed to update the address for user with email: " + email);
        }
        String query = "SELECT * FROM User WHERE UserEmail = ?";
        User updatedUser = jdbcTemplate.queryForObject(query, new Object[] { email }, new UserRowMapper());
        return updatedUser;
    }

    public void SetMember(int userid) {
        jdbcTemplate.update("update User set Member=true where UserId=?", userid);
    }

    public boolean getmember(int id) {
        boolean result = jdbcTemplate.queryForObject("SELECT member FROM User WHERE UserId = ?", new Object[]{id}, Boolean.class);
        return result;
    }
    public User FindByName(String name) {
        User user = jdbcTemplate.queryForObject(
                "SELECT * FROM User WHERE UserName = ?",
                new Object[]{name},
                new BeanPropertyRowMapper<>(User.class)
        );
        return user;
    }
    public void ChangePassword(String email, String password) {
        jdbcTemplate.update("UPDATE User SET UserPwd = ? WHERE UserEmail = ?", password, email);
    }
}
