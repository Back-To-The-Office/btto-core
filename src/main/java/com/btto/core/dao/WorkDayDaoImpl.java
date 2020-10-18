package com.btto.core.dao;

import com.btto.core.domain.User;
import com.btto.core.domain.WorkDay;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class WorkDayDaoImpl extends AbstractJpaDaoImpl<WorkDay> implements WorkDayDao {

    public WorkDayDaoImpl() {
        super(WorkDay.class);
    }

    @Override
    public Optional<WorkDay> findLastWorkDay(final User user, final LocalDate date) {
        final List<WorkDay> items = entityManager.createQuery(
                "from " + WorkDay.class.getName() +
                        " wd where wd.user = :user and wd.startDate = :date order by wd.daySequenceNum desc",
                WorkDay.class)
                .setParameter("user", user)
                .setParameter("date", date)
                .setMaxResults(1)
                .getResultList();
        return items.isEmpty() ? Optional.empty() : Optional.of(items.get(0));
    }
}
