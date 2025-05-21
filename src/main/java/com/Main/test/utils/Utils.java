package com.Main.test.utils;

import com.Main.entity.Classroom;
import com.Main.entity.Course;
import com.Main.entity.User;

public class Utils {

    public static int getRandomId() {
        // 1-127
        return (int) (Math.random() * 127);
    }
    /**
     * generate a course with random name, description, credit, category, hoursPerWeek for debug
     */
    public static Course generateCourse(int teacherId) {
        Course course = new Course();
        course.setName("Course " + getRandomId());
        course.setDescription("Description " + getRandomId());
        course.setTeacherId(teacherId);
        course.setCredit(3.0f);
        course.setCategory("普通");
        course.setHoursPerWeek(3);
        return course;
    }

    /**
     * generate a classroom with random location, capacity, category for debug
     */
    public static Classroom generateClassroom() {
        Classroom classroom = new Classroom();
        classroom.setLocation("Location " + getRandomId());
        classroom.setCapacity(30);
        classroom.setCategory("普通");
        return classroom;
    }

    /**
     * generate a teacher with random name, role, department, contact, avatarPath, account, password for debug
     */
    public static User generateTeacher() {
        User teacher = new User();
        teacher.setName("Teacher " + getRandomId());
        teacher.setAccount("account" + getRandomId());
        teacher.setPassword("password" + getRandomId());
        teacher.setRole("教师");
        teacher.setDepartment("computer science");
        teacher.setContact("1234567890" + getRandomId());
        teacher.setAvatarPath("avatar/teacher" + getRandomId() + ".jpg");
        return teacher;
    }
}
