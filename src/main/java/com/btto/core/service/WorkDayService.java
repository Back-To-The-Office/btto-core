package com.btto.core.service;

import com.btto.core.domain.User;
import com.btto.core.domain.WorkDay;

import java.time.Instant;

public interface WorkDayService extends AbstractEntityService<WorkDay> {
    boolean createOrGetExisting(User owner, Instant startDate);
}
