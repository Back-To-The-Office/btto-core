package com.btto.core.service;

import com.btto.core.dao.UserDao;
import com.btto.core.domain.Company;
import com.btto.core.domain.User;
import com.btto.core.domain.enums.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class UserServiceImpl extends AbstractEntityServiceImpl<User, UserDao> implements UserService {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(final UserDao userDao, final PasswordEncoder passwordEncoder) {
        super(userDao);
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void create(
            final String email,
            final String password,
            final String firstName,
            final String lastName,
            final Role role,
            @Nullable final Company company,
            @Nullable final ZoneId timezone,
            @Nullable final  String position)
    {
        if (exists(email)) {
            throw new ServiceException("User with email " + email + " already exists", ServiceException.Type.ALREADY_EXISTS);
        }
        final User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        user.setCompany(company);
        user.setTimezone(timezone);
        user.setPosition(position);

        dao.create(user);
    }

    @Override
    @Transactional
    public Optional<User> findUserByEmail(final String email) {
        return getUser(email);
    }

    @Override
    @Transactional
    public void deactivateUser(final Integer userId) {
        final User deactivatingUser = Optional.ofNullable(dao.findOne(userId)).orElseThrow(
                () -> new ServiceException("Can't find user with id: " + userId, ServiceException.Type.NOT_FOUND)
        );
        deactivatingUser.setDeactivatedEmail(deactivatingUser.getEmail());
        deactivatingUser.setEmail(UUID.randomUUID().toString().replaceAll("-", ""));
        dao.update(deactivatingUser);
    }

    @Override
    @Transactional
    public User update(
            final Integer userId,
            @Nullable final String oldPassword,
            @Nullable final String newPassword,
            @Nullable final String firstName,
            @Nullable final String lastName,
            @Nullable final ZoneId timezone,
            @Nullable final Role role,
            @Nullable final String position) {
        final User user = Optional.ofNullable(dao.findOne(userId)).orElseThrow(
                () -> new ServiceException("Can't find user with id: " + userId, ServiceException.Type.NOT_FOUND)
        );

        doIfNotNull(newPassword, _newPassword -> {
            checkArgument(StringUtils.isNotBlank(oldPassword));
            checkArgument(StringUtils.isNotBlank(_newPassword));

            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new ServiceException("Wrong old password", ServiceException.Type.WRONG_OLD_PASSWORD);
            }
            user.setPassword(passwordEncoder.encode(_newPassword));
        });
        doIfNotNull(firstName, user::setFirstName);
        doIfNotNull(lastName, user::setLastName);
        doIfNotNull(timezone, user::setTimezone);
        doIfNotNull(role, user::setRole);
        doIfNotNull(position, user::setPosition);

        return dao.update(user);
    }

    @Override
    @Transactional
    public boolean isExists(final String email) {
        return dao.getUserByEmail(email).size() > 0;
    }

    private static <T> void doIfNotNull(@Nullable final T data, final Consumer<T> action) {
        if (data != null) {
            action.accept(data);
        }
    }

    private Optional<User> getUser(final String email) {
        final List<User> usersList = dao.getUserByEmail(email);
        checkNotNull(usersList);
        checkArgument(usersList.size() <= 1);
        return CollectionUtils.isEmpty(usersList) ? Optional.empty() : Optional.of(usersList.get(0));
    }

    private boolean exists(final String email) {
        return !CollectionUtils.isEmpty(dao.getUserByEmail(email));
    }

}
