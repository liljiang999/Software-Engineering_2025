package com.Main.entity;

public class Section {
    private int sectionId;
    private int courseId;
    private int classroomId;
    private int capacity;
    private String semester; // 春夏，秋冬
    private int secYear;
    private String secTime; //开课时间，格式暂定为"Monday 1,2; Wednesday 6,7,8"

    public int getId() {
        return sectionId;
    }

    public void setId(int sectionId) {
        this.sectionId = sectionId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(int classroomId) {
        this.classroomId = classroomId;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getSecYear() {
        return secYear;
    }

    public void setSecYear(int secYear) {
        this.secYear = secYear;
    }

    public String getSecTime() {
        return secTime;
    }

    public void setSecTime(String secTime) {
        this.secTime = secTime;
    }

    @Override
    public String toString() {
        return String.format("Section[id=%d, courseId=%d, classroomId=%d, capacity=%d, semester='%s', year=%d, time='%s']", getId(), getCourseId(), getClassroomId(), getCapacity(), getSemester(), getSecYear(), getSecTime());
    }
}
