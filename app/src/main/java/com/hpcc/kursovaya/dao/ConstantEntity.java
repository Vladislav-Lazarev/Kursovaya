package com.hpcc.kursovaya.dao;

public class ConstantEntity {
    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int TWO = 2;

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String NUMBER = "number";

    public static final int MIN_COUNT_ACADEMIC_HOUR = ONE;// Максимальной(наибольшее) количестов чатов в паре
    public static final int MAX_COUNT_ACADEMIC_HOUR = TWO;// Максимальной(наибольшее) количестов чатов в паре

    public static final int MIN_COUNT_LESSON = 3;// Максимальной(наибольшее) количестов пар в день
    public static final int MAX_COUNT_LESSON = 5;// Максимальной(наибольшее) количестов пар в день

    public static final int MIN_DAY_OF_WEEK = ZERO;
    public static final int MAX_DAY_OF_WEEK = 6;

    public static final int MIN_COUNT_WEEK = MIN_DAY_OF_WEEK + ONE;
    public static final int MAX_COUNT_WEEK = MAX_DAY_OF_WEEK + ONE;

    public static final int MIN_COUNT_COURSE = ONE;
    public static final int MAX_COUNT_COURSE = 4;

    //public static final int MIN_COUNT_SEMESTER_IN_YEAR = ONE;
    //public static final int MAX_COUNT_SEMESTER_IN_YEAR = TWO;
}
