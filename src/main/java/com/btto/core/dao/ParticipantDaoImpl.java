package com.btto.core.dao;

import com.btto.core.domain.Department;
import com.btto.core.domain.Participant;
import com.btto.core.domain.User;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

@Component
public class ParticipantDaoImpl extends AbstractJpaDaoImpl<Participant> implements ParticipantDao {

    public ParticipantDaoImpl() {
        super(Participant.class);
    }

    @Override
    public boolean exists(final User user, final Department department) {
        return !getParticipantsByUserAndDepartment(user, department).isEmpty();
    }

    @Override
    public Optional<Participant> getByUserAndDepartment(final User user, final Department department) {
        final List<Participant> participants = getParticipantsByUserAndDepartment(user, department);
        checkArgument(participants.size() <= 1);

        return CollectionUtils.isEmpty(participants) ? Optional.empty() : Optional.of(participants.get(0));

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
