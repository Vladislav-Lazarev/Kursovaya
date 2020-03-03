package com.hpcc.kursovaya.dao;

public class ConstantEntity {
    public static final int ONE = 1;
    public static final int TWO = 2;

    public static final int MIN_COUNT_ACADEMIC_HOUR = ONE;// Максимальной(наибольшее) количестов чатов в паре
    public static final int MAX_COUNT_ACADEMIC_HOUR = TWO;// Максимальной(наибольшее) количестов чатов в паре

    public static final int MIN_COUNT_LESSON = 3;// Максимальной(наибольшее) количестов пар в день
    public static final int MAX_COUNT_LESSON = 5;// Максимальной(наибольшее) количестов пар в день

    public static final int MIN_WEEK = ONE;
    public static final int MAX_WEEK = 7;

    public static final int MIN_COUNT_SEMESTER = ONE;
    public static final int MAX_COUNT_SEMESTER = (int)Math.pow(TWO, 3);

    public static final int MIN_COUNT_COURSE = MIN_COUNT_SEMESTER;
    public static final int MAX_COUNT_COURSE = MAX_COUNT_SEMESTER / TWO;

    public static final int MIN_COUNT_SEMESTER_IN_YEAR = ONE;
    public static final int MAX_COUNT_SEMESTER_IN_YEAR = TWO;
}
