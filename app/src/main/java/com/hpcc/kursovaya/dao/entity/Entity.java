package com.hpcc.kursovaya.dao.entity;

public interface Entity<T> {
    boolean hasEntity();
    T newEntity();
}
