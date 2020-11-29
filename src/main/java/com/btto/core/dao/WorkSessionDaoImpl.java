package com.btto.core.dao;

import com.btto.core.domain.User;
import com.btto.core.domain.WorkSession;
import org.springframework.stereotype.Component;

import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class WorkSessionDaoImpl extends AbstractJpaDaoImpl<WorkSession> implements WorkSessionDao {

    public WorkSessionDaoImpl() {
        super(WorkSession.class);
    }

    @Override
    public Optional<WorkSession> findLastWorkSession(final User user, final LocalDate date) {
        final List<WorkSession> items = entityManager.createQuery(
                "from " + WorkSession.class.getName() +
                        " wd where wd.user = :user and wd.startDate = :date order by wd.daySequenceNum desc",
                        WorkSession.class)
                .setParameter("user", user)
                .setParameter("date", date)
                .setMaxResults(1)
                .getResultList();
        return items.isEmpty() ? Optional.empty() : Optional.of(items.get(0));
    }

    @Override
    public Optional<WorkSession> findLastActiveWorkSession(final User user) {
        final List<WorkSession> items = entityManager.createQuery(
                "from " + WorkSession.class.getName() +
                        " wd where wd.user = :user and wd.endDate is null order by wd.daySequenceNum desc",
                WorkSession.class)
                .setParameter("user", user)
                .setMaxResults(1)
                .getResultList();
        return items.isEmpty() ? Optional.empty() : Optional.of(items.get(0));
    }

    @Override
    public List<WorkSession> findActiveSessionsByStartDateAndTimezoneOffset(final LocalDate startDate,
                                                                            final long timezoneOffset,
                                                                            final int limit) {
        TypedQuery<WorkSession> query = entityManager.createQuery(
                "from " + WorkSession.class.getName() +
                        " wd where wd.startDate = :date and wd.timezoneOffset = :offset",
                        WorkSession.class)
                .setParameter("date", startDate)
                .setParameter("offset", timezoneOffset);
        if (limit > 0) {
            query = query.setMaxResults(limit);
        }

        return query.getResultList();
    }

    @Override
    public List<WorkSession> findDaySessions(final User user, final LocalDate dayDate) {
        return entityManager.createQuery(
                        "from " + WorkSession.class.getName() +
                        " wd where wd.user = :user and wd.startDate = :date order by wd.daySequenceNum desc",
                        WorkSession.class)
                .setParameter("user", user)
                .setParameter("date", dayDate)
                .getResultList();
    }

}
