package com.Main.service.lesson;

import com.Main.entity.Section;
import com.Main.entity.Course;
import com.Main.entity.Classroom;
import com.Main.RowMapper.SectionRowMapper;
import com.Main.RowMapper.ClassroomRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class LessonScheduler implements AutoManualScheduler, ClassroomManager {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void generateSchedule(List<Course> courses) {
        // TODO: 实现自动排课算法
        for (Course course : courses) {
            // 为每个课程生成课程安排
            // 需要考虑教室容量、时间冲突等因素
        }
    }

    @Override
    public void addSchedule(Section section) {
        String sql = "INSERT INTO section (course_id, classroom_id, capacity, semester, sec_year, sec_time) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, 
            section.getCourseId(),
            section.getClassroomId(),
            section.getCapacity(),
            section.getSemester(),
            section.getSecYear(),
            section.getSecTime()
        );
    }

    @Override
    public void deleteSchedule(int sectionId) {
        String sql = "DELETE FROM section WHERE section_id = ?";
        jdbcTemplate.update(sql, sectionId);
    }

    @Override
    public void updateSchedule(int sectionId, Section updateInfo) {
        String sql = "UPDATE section SET course_id = ?, classroom_id = ?, capacity = ?, semester = ?, sec_year = ?, sec_time = ? WHERE section_id = ?";
        jdbcTemplate.update(sql,
            updateInfo.getCourseId(),
            updateInfo.getClassroomId(),
            updateInfo.getCapacity(),
            updateInfo.getSemester(),
            updateInfo.getSecYear(),
            updateInfo.getSecTime(),
            sectionId
        );
    }

    @Override
    public void checkSchedule() {
        // TODO: 实现课程安排检查逻辑
        // 检查时间冲突、教室容量等
    }

    @Override
    public List<Section> showSchedule() {
        String sql = "SELECT * FROM section";
        return jdbcTemplate.query(sql, new SectionRowMapper());
    }

    @Override
    public void addClassroom(Classroom classroom) {
        String sql = "INSERT INTO classroom (location, capacity) VALUES (?, ?)";
        jdbcTemplate.update(sql, classroom.getLocation(), classroom.getCapacity());
    }

    @Override
    public void deleteClassroom(int classroomId) {
        String sql = "DELETE FROM classroom WHERE classroom_id = ?";
        jdbcTemplate.update(sql, classroomId);
    }

    @Override
    public void updateClassroom(int classroomId, Classroom updateInfo) {
        String sql = "UPDATE classroom SET location = ?, capacity = ? WHERE classroom_id = ?";
        jdbcTemplate.update(sql, updateInfo.getLocation(), updateInfo.getCapacity(), classroomId);
    }

    @Override
    public List<Classroom> queryClassrooms(Classroom classroomFilter) {
        //TODO: 改成ClassroomFilter类
        StringBuilder sql = new StringBuilder("SELECT * FROM classroom WHERE 1=1");
        if (classroomFilter.getLocation() != null) {
            sql.append(" AND location LIKE ?");
        }
        if (classroomFilter.getCapacity() > 0) {
            sql.append(" AND capacity >= ?");
        }
        return jdbcTemplate.query(sql.toString(), new ClassroomRowMapper());
    }


}
