package com.Main.web.lesson;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import com.Main.service.lesson.LessonScheduler;
import com.Main.entity.Classroom;
import com.Main.entity.Section;
import com.Main.entity.lesson.LessonScheduleFilter;
import com.Main.entity.Course;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class LessonController {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private LessonScheduler lessonScheduler;

    @PostMapping("/classrooms")
    public void addClassroom(@RequestBody Classroom classroom) {
        lessonScheduler.addClassroom(classroom);
    }

    @PutMapping("/classrooms/{classroom_id}")
    public void updateClassroom(@PathVariable("classroom_id") int classroomId, @RequestBody Classroom updateInfo) {
        lessonScheduler.updateClassroom(classroomId, updateInfo);
    }

    @DeleteMapping("/classrooms/{classroom_id}")
    public void deleteClassroom(@PathVariable("classroom_id") int classroomId) {
        lessonScheduler.deleteClassroom(classroomId);
    }

    @PostMapping("/classrooms/query")
    public List<Classroom> queryClassrooms(@RequestBody Classroom filter) {
        return lessonScheduler.queryClassrooms(filter);
    }
    
    @PostMapping("/schedules/generate")
    public void generateSchedule(@RequestBody List<Course> courses, @RequestBody LessonScheduleFilter filter) {
        lessonScheduler.generateSchedule(courses, filter);
    }

    @PostMapping("/schedules")
    public void addSchedule(@RequestBody Section section) {
        lessonScheduler.addSchedule(section);
    }

    @PutMapping("/schedules/{section_id}")
    public void updateSchedule(@PathVariable("section_id") int sectionId, @RequestBody Section updateInfo) {
        lessonScheduler.updateSchedule(sectionId, updateInfo);
    }
    
    @DeleteMapping("/schedules/{section_id}")
    public void deleteSchedule(@PathVariable("section_id") int sectionId) {
        lessonScheduler.deleteSchedule(sectionId);
    }

    @GetMapping("/schedules/check")
    public boolean checkSchedule() {
        try{
            lessonScheduler.checkSchedule();
            return true;
        }catch(Exception e){
            logger.warn("检查排课失败", e);
            return false;
        }
    }

    @GetMapping("/schedules")
    public List<Section> getSchedules(@RequestBody Section sectionFilter) {
        return lessonScheduler.showSchedule(sectionFilter);
    }

    @GetMapping("/schedules/teacher/{teacher_id}")
    public List<Section> getSchedulesByTeacherId(@PathVariable("teacher_id") int teacherId) {
        return lessonScheduler.showSchedule(teacherId);
    }
}
