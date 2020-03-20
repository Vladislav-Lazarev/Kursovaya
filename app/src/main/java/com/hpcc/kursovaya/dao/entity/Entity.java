package com.hpcc.kursovaya.dao.entity;

public interface Entity<T> {
    boolean isEntity();
    T newEntity() throws Exception;
}
