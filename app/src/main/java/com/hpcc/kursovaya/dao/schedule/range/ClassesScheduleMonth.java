package com.hpcc.kursovaya.dao.schedule.range;

import java.util.ArrayList;
import java.util.List;

public class ClassesScheduleMonth extends ClassesSchedule {
    private List<ClassesScheduleWeek> classesScheduleWeekList;

    {
        classesScheduleWeekList = new ArrayList<>();
    }
    public ClassesScheduleMonth() {
        super();
    }
    public ClassesScheduleMonth(DateRange dateRange, List<ClassesScheduleWeek> classesScheduleWeekList) {
        super(dateRange);
        setClassesScheduleWeekList(classesScheduleWeekList);
    }

    public List<ClassesScheduleWeek> getClassesScheduleWeekList() {
        return classesScheduleWeekList;
    }
    public ClassesScheduleMonth setClassesScheduleWeekList(List<ClassesScheduleWeek> classesScheduleWeekList) {
        // TODO setClassesScheduleWeekList
        this.classesScheduleWeekList = classesScheduleWeekList;
        return this;
    }

    @Override
    public String toString() {
        return "ClassesScheduleMonth{" +
                "classesScheduleWeekList=" + classesScheduleWeekList +
                ", dateRange=" + dateRange +
                '}';
    }
}
