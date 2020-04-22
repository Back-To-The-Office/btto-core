package com.btto.core.service;

import com.btto.core.dao.AbstractJpaDao;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@AllArgsConstructor
public abstract class AbstractEntityServiceImpl<Entity, Dao extends AbstractJpaDao<Entity>> implements AbstractEntityService<Entity> {

    public final Dao dao;

    @Override
    @Transactional
    public Optional<Entity> find(Integer id) {
        return Optional.ofNullable(dao.findOne(id));
    }
}
