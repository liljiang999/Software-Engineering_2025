package com.Main.entity;

public class Course {
    private int CourseId;
    private String CourseName;
    private String CourseDescription;
    private int TeacherId;
    private float Credit;
    private String Category;

    public int getId() {
        return CourseId;
    }

    public void setId(int CourseId) {
        this.CourseId = CourseId;
    }

    public String getName() {
        return CourseName;
    }

    public void setName(String CourseName) {
        this.CourseName = CourseName;
    }

    public String getDescription() {
        return CourseDescription;
    }

    public void setDescription(String CourseDescription) {
        this.CourseDescription = CourseDescription;
    }

    public int getTeacherId() {
        return TeacherId;
    }   

    public void setTeacherId(int TeacherId) {
        this.TeacherId = TeacherId;
    }

    public float getCredit() {
        return Credit;
    }

    public void setCredit(float Credit) {
        this.Credit = Credit;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String Category) {
        this.Category = Category;
    }

    @Override
    public String toString() {
        return String.format("Course[id=%d, name='%s', description='%s', teacherId=%d, credit=%f, category='%s']", getId(), getName(), getDescription(), getTeacherId(), getCredit(), getCategory());
    }
}
