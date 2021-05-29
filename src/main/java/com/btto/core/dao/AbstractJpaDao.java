package com.btto.core.dao;

import java.util.List;

public interface AbstractJpaDao<T> {
    void delete(T entity);

    T merge(T entity);

    Integer create(T entity);

    T findOne(int id);

    List<T> findAll();

    void deleteById(int id);

    boolean exists(int id);
}
