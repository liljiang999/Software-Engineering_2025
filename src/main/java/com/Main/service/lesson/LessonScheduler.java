package com.Main.service.lesson;

import com.Main.entity.Section;
import com.Main.entity.Course;
import com.Main.entity.Classroom;
import com.Main.RowMapper.SectionRowMapper;
import com.Main.RowMapper.ClassroomRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.HashMap;
import com.Main.entity.lesson.LessonScheduleFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.Main.RowMapper.CourseRowMapper;

@Component
public class LessonScheduler implements AutoManualScheduler, ClassroomManager {
    
    //logger
    private static final Logger logger = LoggerFactory.getLogger(LessonScheduler.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private void deleteCurrentSchedule(String semester, int secYear) {
        String sql = "DELETE FROM section WHERE semester = ? AND sec_year = ?";
        jdbcTemplate.update(sql, semester, secYear);
    }

    private class Arrangement implements Comparable<Arrangement> {
        //周几
        public enum Week{
            MONDAY(1, "Monday"),
            TUESDAY(2, "Tuesday"),
            WEDNESDAY(3, "Wednesday"),
            THURSDAY(4, "Thursday"),
            FRIDAY(5, "Friday"),
            SATURDAY(6, "Saturday"),
            SUNDAY(7, "Sunday");

            private final int value;
            private final String name;

            Week(int value, String name) {
                this.value = value;
                this.name = name;
            }

            public int getValue() {
                return value;
            }

            public String getName() {
                return name;
            }

            public static Week fromInt(int value) {
                for (Week week : Week.values()) {
                    if (week.value == value) {
                        return week;
                    }
                }
                throw new IllegalArgumentException("Invalid week value: " + value);
            }

            public static Week fromString(String name) {
                for (Week week : Week.values()) {
                    if (week.name.equalsIgnoreCase(name)) {
                        return week;
                    }
                }
                throw new IllegalArgumentException("Invalid week name: " + name);
            }
        };
        //节数
        Week week;
        public String time;//"1,2,3"
        public double arrangedCount;//当前时间点已经安排的课程数(可能是三节课的平均值)
        Arrangement(Week week, String time, double arrangedCount){
            this.week = week;
            this.time = time;
            this.arrangedCount = arrangedCount;
        }
        Arrangement(int week, String time, double arrangedCount){
            this.week = Week.fromInt(week);
            this.time = time;
            this.arrangedCount = arrangedCount;
        }
        //从小到大排序
        @Override
        public int compareTo(Arrangement o) {
            return Double.compare(this.arrangedCount, o.arrangedCount);
        }
    }

    private void addPossibleTimeToHeap(PriorityQueue<Arrangement> heap,int hoursPerWeek, int[][] record) {
        //根据连堂节数判断可以安排的时间
        //1.连堂三节，可以安排的时间为：
        //   - 上午：3 4 5
        //   - 下午：6 7 8
        //   - 晚上：11 12 13
        if(hoursPerWeek == 3){
            for(int i = 1; i <= 5; i++){
                heap.add(new Arrangement(i, "3,4,5", (record[i][3] + record[i][4] + record[i][5]) / 3.0));
                heap.add(new Arrangement(i, "6,7,8", (record[i][6] + record[i][7] + record[i][8]) / 3.0));
                heap.add(new Arrangement(i, "11,12,13", (record[i][11] + record[i][12] + record[i][13]) / 3.0));
            }
            //周末降低优先级
            for(int i = 6; i <= 7; i++){
                heap.add(new Arrangement(i, "3,4,5", (record[i][3] + record[i][4] + record[i][5]) / 3.0 * 10));
                heap.add(new Arrangement(i, "6,7,8", (record[i][6] + record[i][7] + record[i][8]) / 3.0 * 10));
                heap.add(new Arrangement(i, "11,12,13", (record[i][11] + record[i][12] + record[i][13]) / 3.0 * 10)); 
            }
        }


        //2.连堂两节，可以安排的时间为：
        //   - 上午：1 2
        //   - 上午：3 4
        //   - 下午：6 7
        //   - 下午：7 8
        //   - 晚上：11 12
        if(hoursPerWeek == 2){
            for(int i = 1; i <= 5; i++){
                heap.add(new Arrangement(i, "1,2", (record[i][1] + record[i][2]) / 2.0));
                heap.add(new Arrangement(i, "3,4", (record[i][3] + record[i][4]) / 2.0));
                heap.add(new Arrangement(i, "6,7", (record[i][6] + record[i][7]) / 2.0));
                heap.add(new Arrangement(i, "7,8", (record[i][7] + record[i][8]) / 2.0));
                heap.add(new Arrangement(i, "11,12", (record[i][11] + record[i][12]) / 2.0));
            }
            //周末降低优先级
            for(int i = 6; i <= 7; i++){
                heap.add(new Arrangement(i, "1,2", (record[i][1] + record[i][2]) / 2.0 * 10));
                heap.add(new Arrangement(i, "3,4", (record[i][3] + record[i][4]) / 2.0 * 10));
                heap.add(new Arrangement(i, "6,7", (record[i][6] + record[i][7]) / 2.0 * 10));
                heap.add(new Arrangement(i, "7,8", (record[i][7] + record[i][8]) / 2.0 * 10));
                heap.add(new Arrangement(i, "11,12", (record[i][11] + record[i][12]) / 2.0 * 10));
            }
        }
        //3.连堂一节，可以安排的时间为：
        //   1-13 均可
        if(hoursPerWeek == 1){
            for(int i = 1; i <= 5; i++){
                for(int j = 1; j <= 13; j++){
                    heap.add(new Arrangement(i, String.valueOf(j), (record[i][j]) / 1.0));
                }
            }
            for(int i = 6; i <= 7; i++){
                for(int j = 1; j <= 13; j++){
                    heap.add(new Arrangement(i, String.valueOf(j), (record[i][j]) / 1.0 * 10));
                }
            }
        }

    }


    @Override
    public void generateSchedule(LessonScheduleFilter filter) {
        List<Integer> courseIds = filter.getCourses();
        List<Course> undistributedCourses = new ArrayList<>();
        for(Integer courseId : courseIds){
            //从course表中查询courseId对应的课程
            String sql = "SELECT * FROM course WHERE course_id = ?";
            Course course = jdbcTemplate.queryForObject(sql, new CourseRowMapper(), courseId);
            undistributedCourses.add(course);
        }
        var semester = filter.getSemester();
        var secYear = filter.getSecYear();
        // 排课之前，删除掉当前学期、学年的课程安排
        deleteCurrentSchedule(semester, secYear);
        List<Course>courses = new ArrayList<>();
        //拆分学时>3的课程
        for (Course course : undistributedCourses) {
            if(course.getHoursPerWeek() == 4){
                courses.add(new Course(course, 2));
                courses.add(new Course(course, 2));
            }
            else if(course.getHoursPerWeek() == 5){
                courses.add(new Course(course, 3));
                courses.add(new Course(course, 2));
            }
            else if(course.getHoursPerWeek() == 6){
                courses.add(new Course(course, 3));
                courses.add(new Course(course, 3));
            }
            else if(course.getHoursPerWeek() == 7){
                courses.add(new Course(course, 3));
                courses.add(new Course(course, 2));
                courses.add(new Course(course, 2));
            }
            else {
                courses.add(course);
            }
        }

        //按照hoursPerWeek排序courses
        Collections.sort(courses, new Comparator<Course>() {
            @Override
            public int compare(Course c1, Course c2) {
                return Integer.compare(c2.getHoursPerWeek(), c1.getHoursPerWeek());
            }
        });
        
        int[][] record = new int[8][20];
        Map<Integer, boolean[][]> teacherTimeMap = new HashMap<>();
        List<Section> sections = new ArrayList<>();
        
        for (Course course : courses) {
            int teacherId = course.getTeacherId();
            if (!teacherTimeMap.containsKey(teacherId)) {
                teacherTimeMap.put(teacherId, new boolean[8][20]);
                boolean[][] teacherTime = teacherTimeMap.get(teacherId);
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 20; j++) {
                        teacherTime[i][j] = false;
                    }
                }
            }
            var heap = new PriorityQueue<Arrangement>();
            addPossibleTimeToHeap(heap, course.getHoursPerWeek(), record);
            boolean successArrange = false;
            do {
                Arrangement times = heap.poll();
                var teacherTime = teacherTimeMap.get(teacherId);
                var timeList = times.time.split(",");
                boolean conflict = false;
                for(String time : timeList){
                    if(teacherTime[times.week.getValue()][Integer.parseInt(time)]){
                        conflict = true;
                        break;
                    }
                }
                if(!conflict){
                    String finalTime = "";
                    for(String time : timeList){
                        teacherTime[times.week.getValue()][Integer.parseInt(time)] = true;
                        record[times.week.getValue()][Integer.parseInt(time)] += 1;
                        finalTime += times.week.getName() + " " + time + "; ";
                    }
                    finalTime = finalTime.substring(0, finalTime.length() - 2);
                    sections.add(new Section(course.getId(), -1, -1, semester, secYear, finalTime, -1));
                    successArrange = true;
                }
            } while (!heap.isEmpty() && !successArrange);
            if(!successArrange){
                throw new RuntimeException("无法安排课程");
            }
        }

        //安排教室
        Map<Integer, boolean[][]> classroomTimeMap = new HashMap<>();
        List<Classroom> classrooms = queryClassrooms(new Classroom());
        for(Section section : sections){
            String sectionCategory = getCourseCategory(section.getCourseId());
            for(Classroom classroom : classrooms){
               
                if(!classroom.getCategory().equals(sectionCategory)){
                    continue;
                }
                if(!classroomTimeMap.containsKey(classroom.getId())){
                    classroomTimeMap.put(classroom.getId(), new boolean[8][20]);
                    boolean[][] classroomTime = classroomTimeMap.get(classroom.getId());
                    for(int i = 0; i < 8; i++){
                        for(int j = 0; j < 20; j++){
                            classroomTime[i][j] = false;
                        }
                    }
                }
                var dayStringList = section.getSecTime().split("; ");
                var classroomTime = classroomTimeMap.get(classroom.getId());
                boolean conflict = false;
                for(String dayString : dayStringList){
                    //dayString: "Monday 1,2"
                    //day: 1
                    //timeList: "1,2"
                    var day = Arrangement.Week.fromString(dayString.split(" ")[0]).getValue();
                    var timeList = dayString.split(" ")[1].split(",");
                    for(String time : timeList){
                        if(classroomTime[day][Integer.parseInt(time)]){
                            conflict = true;
                            break;
                        }
                    }
                    if(conflict){
                        break;
                    }
                }
                if(!conflict){
                    section.setClassroomId(classroom.getId());
                    section.setCapacity(classroom.getCapacity());
                    section.setAvailableCapacity(classroom.getCapacity());
                    for(String dayString : dayStringList){
                        var day = Arrangement.Week.fromString(dayString.split(" ")[0]).getValue();
                        var timeList = dayString.split(" ")[1].split(",");
                        for(String time : timeList){
                            classroomTime[day][Integer.parseInt(time)] = true;
                        }
                    }
                    break;
                }
            } 
        }

        logger.info("安排教室完成");
        
        
        //插入数据库
        for(Section section : sections){
            addSchedule(section);
        }
    }

    private String getCourseCategory(int courseId){
        String sql = "SELECT category FROM course WHERE course_id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, courseId);
    }

    public List<Course> showCourses(){
        String sql = "SELECT * FROM course";
        return jdbcTemplate.query(sql, new CourseRowMapper());
    }

    @Override
    public void addSchedule(Section section) {
        String sql = "INSERT INTO section (course_id, classroom_id, capacity, semester, sec_year, sec_time, available_capacity) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, 
            section.getCourseId(),
            section.getClassroomId(),
            section.getCapacity(),
            section.getSemester(),
            section.getSecYear(),
            section.getSecTime(),
            section.getAvailableCapacity()
        );
    }

    @Override
    public void deleteSchedule(int sectionId) {
        String sql = "DELETE FROM section WHERE section_id = ?";
        jdbcTemplate.update(sql, sectionId);
    }

    @Override
    public void updateSchedule(int sectionId, Section updateInfo) {
        String sql = "UPDATE section SET course_id = ?, classroom_id = ?, capacity = ?, semester = ?, sec_year = ?, sec_time = ? WHERE section_id = ?";
        jdbcTemplate.update(sql,
            updateInfo.getCourseId(),
            updateInfo.getClassroomId(),
            updateInfo.getCapacity(),
            updateInfo.getSemester(),
            updateInfo.getSecYear(),
            updateInfo.getSecTime(),
            sectionId
        );
    }

    @Override
    public boolean checkSchedule(String semester, int secYear) {
        //检查教师和教室是否有同一时间上两节课的冲突
        var sectionFilter = new Section();
        sectionFilter.setSemester(semester);
        sectionFilter.setSecYear(secYear);
        var sectionList = showSchedule(sectionFilter);

        Map<Integer, boolean[][]> teacherTimeMap = new HashMap<>();
        Map<Integer, boolean[][]> classroomTimeMap = new HashMap<>();
        for(Section section : sectionList){
            var teacherId = section.getCourseId();
            var classroomId = section.getClassroomId();
            if(!teacherTimeMap.containsKey(teacherId)){
                teacherTimeMap.put(teacherId, new boolean[8][20]);
                boolean[][] teacherTime = teacherTimeMap.get(teacherId);
                for(int i = 0; i < 8; i++){
                    for(int j = 0; j < 20; j++){
                        teacherTime[i][j] = false;
                    }
                }
            }
            if(!classroomTimeMap.containsKey(classroomId)){
                classroomTimeMap.put(classroomId, new boolean[8][20]);
                boolean[][] classroomTime = classroomTimeMap.get(classroomId);
                for(int i = 0; i < 8; i++){
                    for(int j = 0; j < 20; j++){
                        classroomTime[i][j] = false;
                    }
                }
            }
            var secTime = section.getSecTime();
            var dayStringList = secTime.split("; ");
            for(String dayString : dayStringList){
                var day = Integer.parseInt(dayString.split(" ")[0]);
                var timeList = dayString.split(" ")[1].split(",");
                for(String time : timeList){
                    if(teacherTimeMap.get(teacherId)[day][Integer.parseInt(time)]){
                        logger.error("教师{}在星期{},第{}节课有冲突", teacherId, day, time);
                        return false;
                    }
                    if(classroomTimeMap.get(classroomId)[day][Integer.parseInt(time)]){
                        logger.error("教室{}在星期{},第{}节课有冲突", classroomId, day, time);
                        return false;
                    }
                    teacherTimeMap.get(teacherId)[day][Integer.parseInt(time)] = true;
                    classroomTimeMap.get(classroomId)[day][Integer.parseInt(time)] = true;
                }
            }
        }
        return true;
    }

    @Override
    public List<Section> showSchedule(Section sectionFilter) {
        StringBuilder sql = new StringBuilder("SELECT * FROM section WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (sectionFilter.getId() != -1) {
            sql.append(" AND section_id = ?");
            params.add(sectionFilter.getId());
        }
        if (sectionFilter.getCourseId() != -1) {
            sql.append(" AND course_id = ?");
            params.add(sectionFilter.getCourseId());
        }
        if (sectionFilter.getClassroomId() != -1) {
            sql.append(" AND classroom_id = ?");
            params.add(sectionFilter.getClassroomId());
        }
        if (sectionFilter.getSemester() != null) {
            sql.append(" AND semester = ?");
            params.add(sectionFilter.getSemester());
        }
        if (sectionFilter.getSecYear() != -1) {
            sql.append(" AND sec_year = ?");
            params.add(sectionFilter.getSecYear());
        }
        if (sectionFilter.getSecTime() != null) {
            sql.append(" AND sec_time = ?");
            params.add(sectionFilter.getSecTime());
        }
        if (sectionFilter.getCapacity() != -1) {
            sql.append(" AND capacity = ?");
            params.add(sectionFilter.getCapacity());
        }
        
        return jdbcTemplate.query(sql.toString(), params.toArray(), new SectionRowMapper());
    }

    @Override
    public List<Section> showSchedule(int teacherId) {
        //teacherId 仅在 course 表中存在
        String sql = "SELECT * FROM section WHERE course_id IN (SELECT course_id FROM course WHERE teacher_id = ?)";
        return jdbcTemplate.query(sql, new SectionRowMapper(), teacherId);
    }

    @Override
    public void addClassroom(Classroom classroom) {
        String sql = "INSERT INTO classroom (location, capacity, category) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, classroom.getLocation(), classroom.getCapacity(), classroom.getCategory());
    }

    @Override
    public void deleteClassroom(int classroomId) {
        String sql = "DELETE FROM classroom WHERE classroom_id = ?";
        jdbcTemplate.update(sql, classroomId);
    }

    @Override
    public void updateClassroom(int classroomId, Classroom updateInfo) {
        String sql = "UPDATE classroom SET location = ?, capacity = ?, category = ? WHERE classroom_id = ?";
        jdbcTemplate.update(sql, updateInfo.getLocation(), updateInfo.getCapacity(), updateInfo.getCategory(), classroomId);
    }

    @Override
    public List<Classroom> queryClassrooms(Classroom classroomFilter) {
        StringBuilder sql = new StringBuilder("SELECT * FROM classroom WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (classroomFilter.getId() != -1) {
            sql.append(" AND classroom_id = ?");
            params.add(classroomFilter.getId());
        }
        if (classroomFilter.getLocation() != null) {
            sql.append(" AND location LIKE ?");
            params.add("%" + classroomFilter.getLocation() + "%");
        }
        if (classroomFilter.getCapacity() > 0) {
            sql.append(" AND capacity >= ?");
            params.add(classroomFilter.getCapacity());
        }
        if (classroomFilter.getCategory() != null) {
            sql.append(" AND category = ?");
            params.add(classroomFilter.getCategory());
        }
        return jdbcTemplate.query(sql.toString(), params.toArray(), new ClassroomRowMapper());
    }
}
