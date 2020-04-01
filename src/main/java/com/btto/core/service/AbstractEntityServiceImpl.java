package com.btto.core.service;

import com.btto.core.dao.AbstractJpaDao;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@AllArgsConstructor
public abstract class AbstractEntityServiceImpl<T> implements AbstractEntityService<T> {

    private final AbstractJpaDao<T> dao;

    @Override
    @Transactional
    public Optional<T> find(Integer id) {
        return Optional.ofNullable(dao.findOne(id));
    }
}
