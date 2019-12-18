package com.btto.core.dao;

import com.btto.core.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserDao extends AbstractJpaDao<User> {

    public UserDao() {
        super(User.class);
    }

}
