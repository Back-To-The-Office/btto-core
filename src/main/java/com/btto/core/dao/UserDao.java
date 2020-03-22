package com.btto.core.dao;

import com.btto.core.domain.User;

import java.util.List;

public interface UserDao extends AbstractJpaDao<User> {
    List<User> getUserByEmail(String email);
}
