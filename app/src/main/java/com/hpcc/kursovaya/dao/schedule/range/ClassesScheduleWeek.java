package com.hpcc.kursovaya.dao.schedule.range;

import com.hpcc.kursovaya.dao.schedule.classes.template.TemplateScheduleWeek;

import java.util.ArrayList;
import java.util.List;

public class ClassesScheduleWeek extends ClassesSchedule {
    private List<TemplateScheduleWeek> templateScheduleWeekList;

    {
        templateScheduleWeekList = new ArrayList<>();
    }
    public ClassesScheduleWeek() {
        super();
    }
    public ClassesScheduleWeek(DateRange dateRange, List<TemplateScheduleWeek> templateScheduleWeekList) {
        super(dateRange);
        setTemplateScheduleWeekList(templateScheduleWeekList);
    }

    public List<TemplateScheduleWeek> getTemplateScheduleWeekList() {
        return templateScheduleWeekList;
    }
    public ClassesScheduleWeek setTemplateScheduleWeekList(List<TemplateScheduleWeek> templateScheduleWeekList) {
        // TODO setTemplateScheduleWeekList
        this.templateScheduleWeekList = templateScheduleWeekList;
        return this;
    }

    @Override
    public String toString() {
        return "ClassesScheduleWeek{" +
                "templateScheduleWeekList=" + templateScheduleWeekList +
                ", dateRange=" + dateRange +
                '}';
    }
}
