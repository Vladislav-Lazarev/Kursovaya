package com.hpcc.kursovaya.dao.entity;

public interface EntityI<T> {
    boolean isEntity();
    T newEntity();
}
