package com.hpcc.kursovaya.ui.subjects;

public class SubjectEntity {
    public SubjectEntity(){
        course = 0;
        speciality="";
        name = "";
    }

    public SubjectEntity(String speciality, int course, String name) {
        this.speciality = speciality;
        this.course = course;
        this.name = name;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String speciality;
    private int course;
    private String name;
}
