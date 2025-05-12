package com.Main.entity;

public class Classroom {
    private int ClassroomId;
    private String location;
    private int capacity;

    public int getId() {
        return ClassroomId;
    }

    public void setId(int ClassroomId) {
        this.ClassroomId = ClassroomId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return String.format("Classroom[id=%d, location='%s', capacity=%d]", getId(), getLocation(), getCapacity());
    }
}
