package com.Main.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Main.entity.Classroom;
import com.Main.service.ClassroomDAO;

@RestController
public class ClassroomTester {
    @Autowired
    private ClassroomDAO classroomDAO;

    @GetMapping("/classrooms")
    public List<Classroom> getAllClassrooms() {
        return classroomDAO.getAllClassrooms();
    }
}