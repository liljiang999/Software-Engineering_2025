package com.Main.entity.lesson;

import java.util.List;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;

public class LessonScheduleFilter {

    public enum Priority {
        TEACHER_PREFERENCE,
        CLASSROOM_DEVICE
    } 
    
    private String semester;
    private int secYear;
    private boolean avoidConsecutiveLessons;
    
 
    @JsonDeserialize(converter = StringToPriorityConverter.class)
    private List<Priority> priority;


    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getSecYear() {
        return secYear;
    }

    public void setSecYear(int secYear) {
        this.secYear = secYear;
    }

    public List<Priority> getPriority() {
        return priority;
    }

    public void setPriority(List<Priority> priority) {
        this.priority = priority;
    }

    public boolean isAvoidConsecutiveLessons() {
        return avoidConsecutiveLessons;
    }

    public void setAvoidConsecutiveLessons(boolean avoidConsecutiveLessons) {
        this.avoidConsecutiveLessons = avoidConsecutiveLessons;
    }

}

class StringToPriorityConverter extends StdConverter<List<String>, List<LessonScheduleFilter.Priority>> {
    @Override
    public List<LessonScheduleFilter.Priority> convert(List<String> value) {
        return value.stream()
            .map(String::toUpperCase)
            .map(LessonScheduleFilter.Priority::valueOf)
            .toList();
    }
}
