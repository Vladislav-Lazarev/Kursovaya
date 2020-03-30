package com.hpcc.kursovaya.dao.entity;

import java.util.List;

public interface EntityI<E> {
    boolean isEntity();
    boolean createEntity();
    default <T> T entityToNameList() {
        return null;
    }
    default List<String> entityToNameList(List<E> entityList){
        return null;
    }
}
