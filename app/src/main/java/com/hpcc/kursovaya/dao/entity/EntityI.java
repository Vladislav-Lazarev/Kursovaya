package com.hpcc.kursovaya.dao.entity;

public interface EntityI<E> {
    int getId();

    boolean existsEntity();
    boolean isEntity();
    void checkEntity() throws Exception;
    E createEntity() throws Exception;

    default <T> T entityToNameList() {
        return null;
    }
}
