package com.Main.entity;

public class Course {
    private int courseId;
    private String courseName;
    private String courseDescription;
    private int teacherId;
    private float credit;
    private String category;

    public int getId() {
        return courseId;
    }

    public void setId(int courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return courseName;
    }

    public void setName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return courseDescription;
    }

    public void setDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public int getTeacherId() {
        return teacherId;
    }   

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public float getCredit() {
        return credit;
    }

    public void setCredit(float credit) {
        this.credit = credit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return String.format("Course[id=%d, name='%s', description='%s', teacherId=%d, credit=%f, category='%s']", getId(), getName(), getDescription(), getTeacherId(), getCredit(), getCategory());
    }
}
