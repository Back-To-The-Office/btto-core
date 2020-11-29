package com.btto.core.service;

import com.btto.core.domain.User;
import com.btto.core.domain.WorkSession;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkSessionService extends AbstractEntityService<WorkSession> {

    boolean createActiveSession(User owner);

    Optional<WorkSession> getLastWorkSession(User owner);

    List<WorkSession> getDaySessions(User owner, Instant day);

    WorkSession editSession(Integer workSessionId, Instant startDateTime, Instant endDateTime);

    void closeActiveSession(User owner);

    List<WorkSession> findActiveSessionsByStartDateAndTimezoneOffset(LocalDate startDate,
                                                                     long timezoneOffset,
                                                                     int limit);

    User getWorkSessionOwner(Integer sessionId);

    void deleteSession(Integer workSessionId);
}
