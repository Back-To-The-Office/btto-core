package com.btto.core.dao;

import com.btto.core.domain.User;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;


@Component
public class UserDao extends AbstractJpaDao<User> {

    public UserDao() {
        super(User.class);
    }

    public List<User> getUserByEmail(final String email) {
        return entityManager.createQuery("from " + User.class.getName() + " u where u.email = :email and u.deactivatedEmail is null", User.class)
                .setParameter("email", email)
        .getResultList();
    }

    @Override
    public User update(User entity) {
        entity.setLastUpdate(Instant.now());
        return super.update(entity);
    }
}
