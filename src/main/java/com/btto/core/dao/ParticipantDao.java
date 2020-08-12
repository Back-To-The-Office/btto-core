package com.btto.core.dao;

import com.btto.core.domain.Department;
import com.btto.core.domain.Participant;
import com.btto.core.domain.User;

import java.util.Optional;

public interface ParticipantDao extends AbstractJpaDao<Participant> {
    boolean exists(User user, Department department);

    Optional<Participant> getByUserAndDepartment(User user, Department department);
}
