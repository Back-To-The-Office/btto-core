package com.btto.core.service;

import com.btto.core.domain.User;
import com.btto.core.domain.enums.Role;
import lombok.Getter;

import java.util.Optional;

public interface UserService {
    void create(String email, String password, String firstName, String lastName, Role role);

    Optional<User> findUserByEmail(String email);

    void changePassword(String email, String oldPassword, String newPassword);

    void deactivateUser(String email);

    @Getter
    class UserServiceException extends RuntimeException {
        private final Type type;

        public UserServiceException(String message, Type type) {
            super(message);
            this.type = type;
        }

        public enum Type {
            NotFound, WrongOldPassword
        }
    }
}
