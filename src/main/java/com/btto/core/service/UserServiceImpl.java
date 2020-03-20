package com.btto.core.service;

import com.btto.core.dao.UserDao;
import com.btto.core.domain.User;
import com.btto.core.domain.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class UserServiceImpl implements UserService {
    private static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(@Autowired final UserDao userDao,
                           @Autowired final PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void create(final String email, final String password, final String firstName, final String lastName, final Role role) {
        createImpl(email, password, firstName, lastName, role);
    }

    @Override
    public Optional<User> findUserByEmail(final String email) {
        return getSingleUser(email);
    }

    @Override
    public void changePassword(final String email, final String oldPassword, final String newPassword) {
        final User user = getSingleUser(email)
                .orElseThrow(() ->new UserServiceException("Can't find user with email " + email, UserServiceException.Type.NotFound));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UserServiceException("Wrong old password", UserServiceException.Type.WrongOldPassword);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userDao.update(user);
    }

    @Override
    public void deactivateUser(final String email) {
        final User userToDelete = getSingleUser(email)
                .orElseThrow(() ->new UserServiceException("Can't find user with email " + email, UserServiceException.Type.NotFound));
        userToDelete.setDeactivatedEmail(userToDelete.getEmail());
        userToDelete.setEmail(UUID.randomUUID().toString().replaceAll("-", ""));
        userDao.update(userToDelete);
    }

    private void createImpl(final String email, final String password, final String firstName, final String lastName, final Role role) {
        final User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);

        userDao.create(user);
    }

    private Optional<User> getSingleUser(final String email) {
        final List<User> usersList = userDao.getUserByEmail(email);
        checkNotNull(usersList);
        checkArgument(usersList.size() <= 1);
        return CollectionUtils.isEmpty(usersList) ? Optional.empty() : Optional.of(usersList.get(0));
    }

}
