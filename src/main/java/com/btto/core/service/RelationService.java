package com.btto.core.service;

import com.btto.core.domain.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public interface RelationService {
    boolean isDirectManager(final User manager, final User user);
    boolean isManager(final User manager, final User user);

    @Transactional
    Set<User> getAllManagers(User user);
}
