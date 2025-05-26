package com.Main.web.lesson;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping("/api/classrooms")
    public ResponseEntity<?> addClassroom(@RequestBody Classroom classroom) {
        try {
            logger.info("controller add classroom: " + classroom);
            lessonScheduler.addClassroom(classroom);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("添加教室失败", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "添加教室失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/api/classrooms/{classroom_id}")
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

    @DeleteMapping("/api/classrooms/{classroom_id}")
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

    @GetMapping("/api/classrooms/query")
    public ResponseEntity<?> queryClassrooms(
            @RequestParam(required = false) Integer classroom_id,
            @RequestParam(required = false) String classroom_location,
            @RequestParam(required = false) Integer classroom_capacity,
            @RequestParam(required = false) String classroom_category) {
        try {
            Classroom filter = new Classroom();
            if (classroom_id != null) filter.setId(classroom_id);
            if (classroom_location != null) filter.setLocation(classroom_location);
            if (classroom_capacity != null) filter.setCapacity(classroom_capacity);
            if (classroom_category != null) filter.setCategory(classroom_category);
            
            List<Classroom> classrooms = lessonScheduler.queryClassrooms(filter);
            return ResponseEntity.ok(classrooms);
        } catch (Exception e) {
            logger.error("查询教室失败", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "查询教室失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/api/sections/generate")
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

    @PostMapping("/api/sections")
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

    @PutMapping("/api/sections/{section_id}")
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
    
    @DeleteMapping("/api/sections/{section_id}")
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

    @GetMapping("/api/sections/check")
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

    @GetMapping("/api/sections/query")
    public ResponseEntity<?> getSchedules(
            @RequestParam(required = false) Integer section_id,
            @RequestParam(required = false) Integer course_id,
            @RequestParam(required = false) Integer classroom_id,
            @RequestParam(required = false) Integer capacity,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Integer sec_year,
            @RequestParam(required = false) String sec_time) {
        try {
            Section sectionFilter = new Section();
            if (section_id != null) sectionFilter.setId(section_id);
            if (course_id != null) sectionFilter.setCourseId(course_id);
            if (classroom_id != null) sectionFilter.setClassroomId(classroom_id);
            if (capacity != null) sectionFilter.setCapacity(capacity);
            if (semester != null) sectionFilter.setSemester(semester);
            if (sec_year != null) sectionFilter.setSecYear(sec_year);
            if (sec_time != null) sectionFilter.setSecTime(sec_time);
            List<Section> schedules = lessonScheduler.showSchedule(sectionFilter);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            logger.error("获取课表失败", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "获取课表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/api/sections/teacher/{teacher_id}")
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