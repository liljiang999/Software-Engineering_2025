package com.Main.web.lesson;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.Main.service.lesson.LessonScheduler;
import com.Main.entity.Classroom;
import com.Main.entity.Section;
import com.Main.entity.lesson.LessonScheduleFilter;
import com.Main.entity.Course;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class LessonController {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private LessonScheduler lessonScheduler;

    @PostMapping("/classrooms")
    public ResponseEntity<?> addClassroom(@RequestBody Classroom classroom) {
        try {
            lessonScheduler.addClassroom(classroom);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("添加教室失败", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "添加教室失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/classrooms/{classroom_id}")
    public ResponseEntity<?> updateClassroom(@PathVariable("classroom_id") int classroomId, @RequestBody Classroom updateInfo) {
        try {
            lessonScheduler.updateClassroom(classroomId, updateInfo);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("更新教室失败", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "更新教室失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/classrooms/{classroom_id}")
    public ResponseEntity<?> deleteClassroom(@PathVariable("classroom_id") int classroomId) {
        try {
            lessonScheduler.deleteClassroom(classroomId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("删除教室失败", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "删除教室失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/classrooms/query")
    public ResponseEntity<?> queryClassrooms(@RequestBody Classroom filter) {
        try {
            List<Classroom> classrooms = lessonScheduler.queryClassrooms(filter);
            return ResponseEntity.ok(classrooms);
        } catch (Exception e) {
            logger.error("查询教室失败", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "查询教室失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/schedules/generate")
    public ResponseEntity<?> generateSchedule(@RequestBody List<Course> courses, @RequestBody LessonScheduleFilter filter) {
        try {
            lessonScheduler.generateSchedule(courses, filter);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("生成课表失败", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "生成课表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/schedules")
    public ResponseEntity<?> addSchedule(@RequestBody Section section) {
        try {
            lessonScheduler.addSchedule(section);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("添加课表失败", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "添加课表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/schedules/{section_id}")
    public ResponseEntity<?> updateSchedule(@PathVariable("section_id") int sectionId, @RequestBody Section updateInfo) {
        try {
            lessonScheduler.updateSchedule(sectionId, updateInfo);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("更新课表失败", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "更新课表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/schedules/{section_id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable("section_id") int sectionId) {
        try {
            lessonScheduler.deleteSchedule(sectionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("删除课表失败", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "删除课表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/schedules/check")
    public ResponseEntity<?> checkSchedule(@RequestBody String semester, @RequestBody int secYear) {
        try {
            boolean result = lessonScheduler.checkSchedule(semester, secYear);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("检查课表失败", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "检查课表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/schedules")
    public ResponseEntity<?> getSchedules(@RequestBody Section sectionFilter) {
        try {
            List<Section> schedules = lessonScheduler.showSchedule(sectionFilter);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            logger.error("获取课表失败", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "获取课表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/schedules/teacher/{teacher_id}")
    public ResponseEntity<?> getSchedulesByTeacherId(@PathVariable("teacher_id") int teacherId) {
        try {
            List<Section> schedules = lessonScheduler.showSchedule(teacherId);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            logger.error("获取教师课表失败", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "获取教师课表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}