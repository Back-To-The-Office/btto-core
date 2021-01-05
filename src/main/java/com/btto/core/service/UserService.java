package com.btto.core.service;

import com.btto.core.domain.Company;
import com.btto.core.domain.User;
import com.btto.core.domain.enums.Role;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.time.ZoneId;
import java.util.Optional;

public interface UserService extends AbstractEntityService<User> {

    void create(String email, String password, String firstName, String lastName, Role role, @Nullable Company company,
                @Nullable ZoneId timezone, @Nullable String position);

    Optional<User> findUserByEmail(String email);

    void deactivateUser(Integer userId);

    User update(Integer userId, @Nullable String oldPassword, @Nullable String newPassword, @Nullable String firstName,
                @Nullable String lastName, @Nullable ZoneId timezone, @Nullable Role role, @Nullable final String position);

    @Transactional
    boolean isExists(String email);
}
