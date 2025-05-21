package com.Main.test.lesson;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.ArrayList;
import com.Main.entity.Course;
import com.Main.entity.User;
import com.Main.test.utils.Utils;
import com.Main.RowMapper.CourseRowMapper;
import com.Main.RowMapper.UserRowMapper;

@Component
public class CourseTest {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private void deleteAllCourses() {
        jdbcTemplate.update("DELETE FROM course");
        logger.info("Deleted all courses");
    }

    private void deleteAllTeachers() {
        jdbcTemplate.update("DELETE FROM user WHERE role = '教师'");
        logger.info("Deleted all teachers");
    }

    // Passed
    // @PostConstruct
    public void testInsertAndQueryCourse() {
        // delete all courses
        deleteAllCourses();
        deleteAllTeachers();

        //generate teachers
        List<User> teachers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User teacher = Utils.generateTeacher();
            teachers.add(teacher);
        }
        //insert teachers
        for (User teacher : teachers) {
            logger.info("Inserting teacher: " + teacher.toString());
            jdbcTemplate.update("INSERT INTO user (name, role, department, contact, avatar_path, account, password) VALUES (?, ?, ?, ?, ?, ?, ?)", 
                teacher.getName(), teacher.getRole(), teacher.getDepartment(), teacher.getContact(), teacher.getAvatarPath(), teacher.getAccount(), teacher.getPassword());
            
            // 获取插入后的teacher id
            Long teacherId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
            teacher.setId(teacherId.intValue());
        }

        //generate courses
        List<Course> courses = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Course course = Utils.generateCourse(teachers.get(i).getId());
            courses.add(course);
        }

        //insert courses
        for (Course course : courses) {
            try {
                jdbcTemplate.update("INSERT INTO course (course_name, course_description, teacher_id, credit, category, hours_per_week) VALUES (?, ?, ?, ?, ?, ?)", 
                    course.getName(), 
                    course.getDescription(), 
                    course.getTeacherId(), 
                    course.getCredit(), 
                    course.getCategory(), 
                    course.getHoursPerWeek());
                logger.info("Successfully inserted course: " + course.getName());
            } catch (Exception e) {
                logger.error("Failed to insert course: " + course.getName(), e);
                throw e;
            }
        }

        //query courses
        List<Course> queriedCourses = jdbcTemplate.query("SELECT * FROM course", new CourseRowMapper());
        for (Course course : queriedCourses) {
            logger.info(course.toString());
        }

        //query teachers
        List<User> queriedTeachers = jdbcTemplate.query("SELECT * FROM user WHERE role = '教师'", new UserRowMapper());
        for (User teacher : queriedTeachers) {
            logger.info(teacher.toString());
        }
    }
}
