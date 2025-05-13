package com.Main.service.lesson;

import java.util.List;


import com.Main.entity.Classroom;

public interface ClassroomManager {
    public void addClassroom(Classroom classroom);
    public void deleteClassroom(int classroomId);
    public void updateClassroom(int classroomId, Classroom updateInfo);
    public List<Classroom> queryClassrooms(Classroom classroomFilter);
}