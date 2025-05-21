package com.Main.test.lesson;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import jakarta.annotation.PostConstruct;
import com.Main.service.lesson.LessonScheduler;
import com.Main.test.utils.Utils;
import com.Main.entity.User;
import com.Main.entity.Classroom;
import com.Main.entity.Course;
import com.Main.entity.Section;
import com.Main.entity.lesson.LessonScheduleFilter; 
import java.util.List;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class ArrangeTest {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private LessonScheduler lessonScheduler;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void deleteAllData() {
        //delete all sections
        jdbcTemplate.update("DELETE FROM section");
        //delete all classrooms
        jdbcTemplate.update("DELETE FROM classroom");
        //delete all courses
        jdbcTemplate.update("DELETE FROM course");
        //delete all users
        jdbcTemplate.update("DELETE FROM user");     
    }
    
    @PostConstruct
    public void testNaiveArrange() {
        //delete all data
        deleteAllData();
        //test one course arrange
        testSimpleCourseArrange();

        //test decompose course arrange
        deleteAllData();
        testDecomposeCourseArrange();

        //test multiple courses arrange
        deleteAllData();
        testRandomCoursesArrange();
    }

    private void testSimpleCourseArrange() {
        //generate teacher
        User teacher = Utils.generateTeacher();
        //insert teacher
        jdbcTemplate.update("INSERT INTO user (name, role, department, contact, avatar_path, account, password) VALUES (?, ?, ?, ?, ?, ?, ?)", 
            teacher.getName(), teacher.getRole(), teacher.getDepartment(), teacher.getContact(), teacher.getAvatarPath(), teacher.getAccount(), teacher.getPassword());
        teacher.setId(jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class));
        
        //generate course
        Course course = Utils.generateCourse(teacher.getId());
        //insert course
        jdbcTemplate.update("INSERT INTO course (course_name, course_description, teacher_id, credit, category, hours_per_week) VALUES (?, ?, ?, ?, ?, ?)", 
            course.getName(), course.getDescription(), course.getTeacherId(), course.getCredit(), course.getCategory(), course.getHoursPerWeek());
        course.setId(jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class));
        
        
        //generate classroom
        Classroom classroom = Utils.generateClassroom();
        logger.info("Generated classroom: " + classroom.toString());
        //insert classroom
        jdbcTemplate.update("INSERT INTO classroom (location, capacity, category) VALUES (?, ?, ?)", 
            classroom.getLocation(), classroom.getCapacity(), classroom.getCategory());
        classroom.setId(jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class));


        //generate section
        List<Course> courses = new ArrayList<>();
        courses.add(course);
        //generate filter
        LessonScheduleFilter filter = new LessonScheduleFilter();
        filter.setSemester("春夏");
        filter.setSecYear(2024);
        //generate schedule
        lessonScheduler.generateSchedule(courses, filter);
        //query section
        List<Section> sections = lessonScheduler.showSchedule(teacher.getId());
        //print section
        logger.info("Sections: ");
        for (Section section : sections) {
            logger.info(section.toString());
        }
    }

    private void testDecomposeCourseArrange() {
        //generate teacher
        User teacher = Utils.generateTeacher();
        //insert teacher
        jdbcTemplate.update("INSERT INTO user (name, role, department, contact, avatar_path, account, password) VALUES (?, ?, ?, ?, ?, ?, ?)", 
            teacher.getName(), teacher.getRole(), teacher.getDepartment(), teacher.getContact(), teacher.getAvatarPath(), teacher.getAccount(), teacher.getPassword());
        teacher.setId(jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class));
        
        //generate course
        Course course = Utils.generateCourse(teacher.getId());
        course.setHoursPerWeek(7);
        //insert course
        jdbcTemplate.update("INSERT INTO course (course_name, course_description, teacher_id, credit, category, hours_per_week) VALUES (?, ?, ?, ?, ?, ?)", 
            course.getName(), course.getDescription(), course.getTeacherId(), course.getCredit(), course.getCategory(), course.getHoursPerWeek());
        course.setId(jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class));
        
        
        //generate classroom
        Classroom classroom = Utils.generateClassroom();
        logger.info("Generated classroom: " + classroom.toString());
        //insert classroom
        jdbcTemplate.update("INSERT INTO classroom (location, capacity, category) VALUES (?, ?, ?)", 
            classroom.getLocation(), classroom.getCapacity(), classroom.getCategory());
        classroom.setId(jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class));


        //generate section
        List<Course> courses = new ArrayList<>();
        courses.add(course);
        //generate filter
        LessonScheduleFilter filter = new LessonScheduleFilter();
        filter.setSemester("春夏");
        filter.setSecYear(2024);
        //generate schedule
        lessonScheduler.generateSchedule(courses, filter);
        //query section
        List<Section> sections = lessonScheduler.showSchedule(teacher.getId());
        //print section
        logger.info("Sections: ");
        for (Section section : sections) {
            logger.info(section.toString());
        }
    }

    private void testRandomCoursesArrange() {
        //5 courses, 3 teachers, 3 classrooms
        List<User> teachers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            teachers.add(Utils.generateTeacher());
            jdbcTemplate.update("INSERT INTO user (name, role, department, contact, avatar_path, account, password) VALUES (?, ?, ?, ?, ?, ?, ?)", 
                teachers.get(i).getName(), teachers.get(i).getRole(), teachers.get(i).getDepartment(), teachers.get(i).getContact(), teachers.get(i).getAvatarPath(), teachers.get(i).getAccount(), teachers.get(i).getPassword());
            teachers.get(i).setId(jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class));
        }
        List<Classroom> classrooms = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            classrooms.add(Utils.generateClassroom());
            jdbcTemplate.update("INSERT INTO classroom (location, capacity, category) VALUES (?, ?, ?)", 
                classrooms.get(i).getLocation(), classrooms.get(i).getCapacity(), classrooms.get(i).getCategory());
            classrooms.get(i).setId(jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class));
        }
        List<Course> courses = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            courses.add(Utils.generateCourse(teachers.get(i % 3).getId()));
            courses.get(i).setHoursPerWeek((int)(Math.random() * 10 % 7 + 1));
            jdbcTemplate.update("INSERT INTO course (course_name, course_description, teacher_id, credit, category, hours_per_week) VALUES (?, ?, ?, ?, ?, ?)", 
                courses.get(i).getName(), courses.get(i).getDescription(), courses.get(i).getTeacherId(), courses.get(i).getCredit(), courses.get(i).getCategory(), courses.get(i).getHoursPerWeek());
            courses.get(i).setId(jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class));
        }

        //generate filter
        LessonScheduleFilter filter = new LessonScheduleFilter();
        filter.setSemester("春夏");
        filter.setSecYear(2024);
        //generate schedule
        lessonScheduler.generateSchedule(courses, filter);
        //query section
        for (User teacher : teachers) {
            List<Section> sections = lessonScheduler.showSchedule(teacher.getId());
            logger.info("Sections: ");
            for (Section section : sections) {
                logger.info(section.toString());
            }
        }
    }

}
