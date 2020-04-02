package com.btto.core.service;

import com.btto.core.domain.User;
import com.google.common.collect.ImmutableSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class RelationServiceImpl implements RelationService {

    @Override
    @Transactional
    public boolean isDirectManager(final User manager, final User user) {
        return getDirectManagers(user)
                .map(User::getId)
                .anyMatch(someManagerId -> someManagerId.equals(manager.getId()));
    }

    @Override
    @Transactional
    public boolean isManager(final User manager, final User user) {
        return getAllManagers(user, new HashSet<>())
                .anyMatch(someManager -> someManager.getId().equals(manager.getId()));
    }

    @Override
    @Transactional
    public Set<User> getAllManagers(final User user) {
        return getAllManagers(user, new HashSet<>())
                .collect(ImmutableSet.toImmutableSet());
    }

    private Stream<User> getAllManagers(final User user, final Set<User> subordinates) {
        subordinates.add(user);
        return getDirectManagers(user)
                .filter(manager -> !subordinates.contains(manager))
                .flatMap(manager -> Stream.concat(getAllManagers(manager, subordinates), Stream.of(manager)));
    }

    private Stream<User> getDirectManagers(final User user) {
        return user.getDepartments().stream()
                .filter(department -> department.getOwner().isPresent())
                .map(department -> department.getOwner().get());
    }
}
