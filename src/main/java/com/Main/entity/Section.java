package com.Main.entity;

public class Section {
    private int SectionId;
    private int CourseId;
    private int ClassroomId;
    private int Capacity;
    private String Semester;
    private int SecYear;
    private String SecTime; //开课时间，格式暂定为"Monday 1,2; Wednesday 6,7,8"

    public int getId() {
        return SectionId;
    }

    public void setId(int SectionId) {
        this.SectionId = SectionId;
    }

    public int getCourseId() {
        return CourseId;
    }

    public void setCourseId(int CourseId) {
        this.CourseId = CourseId;
    }

    public int getClassroomId() {
        return ClassroomId;
    }

    public void setClassroomId(int ClassroomId) {
        this.ClassroomId = ClassroomId;
    }

    public int getCapacity() {
        return Capacity;
    }

    public void setCapacity(int Capacity) {
        this.Capacity = Capacity;
    }

    public String getSemester() {
        return Semester;
    }

    public void setSemester(String Semester) {
        this.Semester = Semester;
    }

    public int getSecYear() {
        return SecYear;
    }

    public void setSecYear(int SecYear) {
        this.SecYear = SecYear;
    }

    public String getSecTime() {
        return SecTime;
    }

    public void setSecTime(String SecTime) {
        this.SecTime = SecTime;
    }

    @Override
    public String toString() {
        return String.format("Section[id=%d, courseId=%d, classroomId=%d, capacity=%d, semester='%s', year=%d, time='%s']", getId(), getCourseId(), getClassroomId(), getCapacity(), getSemester(), getSecYear(), getSecTime());
    }
}
