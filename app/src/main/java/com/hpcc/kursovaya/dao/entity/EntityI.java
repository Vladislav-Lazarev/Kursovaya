package com.hpcc.kursovaya.dao.entity;

import java.util.List;

public interface EntityI<E> {
    boolean createEntity();
    <T> T entityToNameList();
   default List<String> entityToNameList(List<E> entityList){
        return null;
    }
}
