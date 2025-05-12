package com.Main.service.lesson;

import com.Main.entity.Section;
import com.Main.entity.Course;
import java.util.List;

public interface AutoManualScheduler {
    public void generateSchedule(List<Course> courses);
    public void addSchedule(Section section);
    public void deleteSchedule(int sectionId);
    public void updateSchedule(int sectionId, Section updateInfo);
    public void checkSchedule();
    public List<Section> showSchedule();
}
