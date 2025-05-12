package com.Main.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.Main.entity.Course;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

public class CourseRowMapper implements RowMapper<Course> {
    @Override
    public Course mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        Course course = new Course();
        course.setId(rs.getInt("course_id"));
        course.setName(rs.getString("course_name"));
        course.setDescription(rs.getString("course_description"));
        return course;
    }
}
