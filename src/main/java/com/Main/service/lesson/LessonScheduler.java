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
import com.Main.entity.lesson.LessonScheduleFilter;

@Component
public class LessonScheduler implements AutoManualScheduler, ClassroomManager {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void generateSchedule(List<Course> courses, LessonScheduleFilter filter) {
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
    public List<Section> showSchedule(Section sectionFilter) {
        StringBuilder sql = new StringBuilder("SELECT * FROM section WHERE 1=1");
        if (sectionFilter.getId() != -1) {
            sql.append(" AND section_id = ?");
        }
        if (sectionFilter.getCourseId() != -1) {
            sql.append(" AND course_id = ?");
        }
        if (sectionFilter.getClassroomId() != -1) {
            sql.append(" AND classroom_id = ?");
        }
        if (sectionFilter.getSemester() != null) {
            sql.append(" AND semester = ?");
        }
        if (sectionFilter.getSecYear() != -1) {
            sql.append(" AND sec_year = ?");
        }
        if (sectionFilter.getSecTime() != null) {
            sql.append(" AND sec_time = ?");
        }
        if (sectionFilter.getCapacity() != -1) {
            sql.append(" AND capacity = ?");
        }
        return jdbcTemplate.query(sql.toString(), new SectionRowMapper());
    }

    @Override
    public List<Section> showSchedule(int teacherId) {
        //teacherId 仅在 course 表中存在
        String sql = "SELECT * FROM section WHERE course_id IN (SELECT course_id FROM course WHERE teacher_id = ?)";
        return jdbcTemplate.query(sql, new SectionRowMapper(), teacherId);
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
        StringBuilder sql = new StringBuilder("SELECT * FROM classroom WHERE 1=1");
        if (classroomFilter.getId() != -1) {
            sql.append(" AND classroom_id = ?");
        }
        if (classroomFilter.getLocation() != null) {
            sql.append(" AND location LIKE ?");
        }
        if (classroomFilter.getCapacity() > 0) {
            sql.append(" AND capacity >= ?");
        }
        return jdbcTemplate.query(sql.toString(), new ClassroomRowMapper());
    }
}
