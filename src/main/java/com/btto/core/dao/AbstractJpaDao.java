package com.btto.core.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public abstract class AbstractJpaDao<T> {
    private final Class<T> clazz;

    @PersistenceContext
    protected EntityManager entityManager;

    public AbstractJpaDao(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T findOne(long id){
        return entityManager.find( clazz, id );
    }
    public List<T> findAll(){
        return entityManager.createQuery("from " + clazz.getName(), clazz)
                .getResultList();
    }

    public void create(T entity ){
        entityManager.persist(entity);
    }

    public T update(T entity){
        return entityManager.merge( entity );
    }

    public void delete(T entity){
        entityManager.remove( entity );
    }
    public void deleteById(long entityId){
        T entity = findOne( entityId );
        delete( entity );
    }
}
