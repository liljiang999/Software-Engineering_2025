package com.Main.test.lesson;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;
import com.Main.entity.Classroom;
import com.Main.service.arrange.LessonScheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
public class ClassroomTest {
    
    @Autowired
    private ArrangeTest arrangeTest;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private LessonScheduler lessonScheduler;


    // Passed
    // @PostConstruct
    public void testInsertAndQueryClassroom() {
        //delete all data
        arrangeTest.deleteAllData();
        // Create classroom object
        logger.info("Starting test for inserting and querying classroom");
        Classroom classroom = new Classroom();
        classroom.setId(1);
        classroom.setCapacity(30);
        classroom.setLocation("Test Classroom");
        classroom.setCategory("Test Classroom");
        logger.info("Created classroom object: {}", classroom);
        // Insert classroom
        lessonScheduler.addClassroom(classroom);
        logger.info("Inserted classroom: {}", classroom);
        // Query classroom
        List<Classroom> queriedClassrooms = lessonScheduler.queryClassrooms(new Classroom());
        logger.info("Queried classroom: {}", queriedClassrooms);
        // Verify results
        Assert.notEmpty(queriedClassrooms, "Query result should not be empty");
        Assert.isTrue(queriedClassrooms.get(0).getLocation().equals("Test Classroom"), "Classroom location does not match");
        Assert.isTrue(queriedClassrooms.get(0).getCapacity() == 30, "Classroom capacity does not match");
        logger.info("Verification results: {}", queriedClassrooms);
        // Delete classroom
        lessonScheduler.deleteClassroom(classroom.getId());
        logger.info("Deleted classroom: {}", classroom);
        // Query again to verify deletion
        queriedClassrooms = lessonScheduler.queryClassrooms(classroom);
        Assert.isTrue(queriedClassrooms.isEmpty(), "Query result should be empty after deletion");
        logger.info("Verification results: {}", queriedClassrooms);
    }
}