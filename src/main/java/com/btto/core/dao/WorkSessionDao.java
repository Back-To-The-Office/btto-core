package com.btto.core.dao;

import com.btto.core.domain.User;
import com.btto.core.domain.WorkSession;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkSessionDao extends AbstractJpaDao<WorkSession> {
    Optional<WorkSession> findLastWorkSession(User user, LocalDate date);

    Optional<WorkSession> findLastActiveWorkSession(User user);

    List<WorkSession> findActiveSessionsByStartDateAndTimezoneOffset(LocalDate startDate,
                                                                     long timezoneOffset,
                                                                     int limit);

    List<WorkSession> findDaySessions(User user, LocalDate dayDate);
}
