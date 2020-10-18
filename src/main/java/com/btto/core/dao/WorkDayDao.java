package com.btto.core.dao;

import com.btto.core.domain.User;
import com.btto.core.domain.WorkDay;
import java.time.LocalDate;
import java.util.Optional;

public interface WorkDayDao extends AbstractJpaDao<WorkDay> {
    Optional<WorkDay> findLastWorkDay(User user, LocalDate date);
}
