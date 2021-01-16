package com.btto.core.dao;

import com.btto.core.domain.EntityWithId;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public abstract class AbstractJpaDaoImpl<T extends EntityWithId> implements AbstractJpaDao<T> {
    private final Class<T> clazz;

    @PersistenceContext
    protected EntityManager entityManager;

    public AbstractJpaDaoImpl(final Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    @Nullable
    public T findOne(final int id){
        return entityManager.find( clazz, id );
    }

    @Override
    public List<T> findAll(){
        return entityManager.createQuery("from " + clazz.getName(), clazz)
                .getResultList();
    }

    @Override
    public Integer create(T entity){
        entityManager.persist(entity);
        entityManager.flush();
        return entity.getId();
    }

    @Override
    public T update(final T entity){
        return entityManager.merge(entity);
    }

    @Override
    public void delete(final T entity){
        entityManager.remove( entity );
    }

    @Override
    public void deleteById(final int id){
        final T entity = findOne(id);
        delete( entity );
    }

    @Override
    public boolean exists(final int id) {
        return findOne(id) != null;
    }
}
