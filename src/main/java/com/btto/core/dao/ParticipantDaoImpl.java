package com.btto.core.dao;

import com.btto.core.domain.Department;
import com.btto.core.domain.Participant;
import com.btto.core.domain.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ParticipantDaoImpl extends AbstractJpaDaoImpl<Participant> implements ParticipantDao {

    public ParticipantDaoImpl() {
        super(Participant.class);
    }

    @Override
    public boolean exists(final User user, final Department department) {
        return !getParticipantsByUserAndDepartment(user, department).isEmpty();
    }

    private List<Participant> getParticipantsByUserAndDepartment(final User user, final Department department) {
        return entityManager.createQuery(
                "from " + Participant.class.getName() + " p where p.participant = :user " +
                        " and p.department = :department ",
                Participant.class)
                .setParameter("userId", user)
                .setParameter("departmentId", department)
                .getResultList();
    }

}
