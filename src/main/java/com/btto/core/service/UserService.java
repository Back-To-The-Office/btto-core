package com.btto.core.service;

import com.btto.core.domain.User;
import com.btto.core.domain.enums.Role;

import java.util.Optional;

public interface UserService extends AbstractEntityService<User> {
    void create(String email, String password, String firstName, String lastName, Role role);

    Optional<User> findUserByEmail(String email);

    void changePassword(String email, String oldPassword, String newPassword);

    void deactivateUser(String email);
}
