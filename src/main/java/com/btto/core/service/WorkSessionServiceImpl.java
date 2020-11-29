package com.btto.core.service;

import com.btto.core.dao.WorkSessionDao;
import com.btto.core.domain.User;
import com.btto.core.domain.WorkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class WorkSessionServiceImpl extends AbstractEntityServiceImpl<WorkSession, WorkSessionDao> implements WorkSessionService {
    @Autowired
    public WorkSessionServiceImpl(WorkSessionDao dao) {
        super(dao);
    }

    @Override
    public boolean createActiveSession(final User owner) {
        // try to get previous records
        final LocalDate currentDay = getCurrentLocalDate(owner.getTimezone());
        final Optional<WorkSession> lastWorkDay = dao.findLastWorkSession(owner, currentDay);
        // if exists, check that it had been closed
        if (lastWorkDay.isPresent() && lastWorkDay.get().getEndDateTime() != null) {
            return false;
        }
        final WorkSession newWorkSession = new WorkSession();
        newWorkSession.setDaySequenceNum(lastWorkDay.map(day -> day.getDaySequenceNum() + 1).orElse(0));
        newWorkSession.setOwner(owner);
        final long offsetInMinutes = TimeUnit.SECONDS.toMinutes(
                owner.getTimezone().getRules().getOffset(Instant.now()).get(ChronoField.OFFSET_SECONDS)
        );
        newWorkSession.setTimezoneOffset(offsetInMinutes);
        newWorkSession.setStartDateTime(Instant.now());
        newWorkSession.setSessionDate(currentDay);
        // create new record
        dao.create(newWorkSession);
        return true;
    }

    @Override
    public Optional<WorkSession> getLastWorkSession(final User owner) {
        return dao.findLastWorkSession(owner, getCurrentLocalDate(owner.getTimezone()));
    }

    @Override
    public List<WorkSession> getDaySessions(final User owner, final Instant day) {
        return dao.findDaySessions(owner, day.atZone(owner.getTimezone()).toLocalDate());
    }

    @Override
    public WorkSession editSession(final Integer workSessionId, final Instant startDateTime, final Instant endDateTime) {
        final WorkSession session = Optional.ofNullable(dao.findOne(workSessionId)).orElseThrow(
                () -> new ServiceException("Can't find work session with id: " + workSessionId, ServiceException.Type.NOT_FOUND)
        );

        final LocalDate newStartDate = getCurrentLocalDate(session.getOwner().getTimezone());

        session.setSessionDate(newStartDate);
        session.setStartDateTime(startDateTime);
        session.setEndDateTime(endDateTime);

        return dao.update(session);
    }

    @Override
    public void closeActiveSession(final User owner) {
        final Optional<WorkSession> activeSession = dao.findLastActiveWorkSession(owner);
        if (!activeSession.isPresent()) {
            throw new ServiceException("Can't find any active session", ServiceException.Type.NOT_FOUND);
        }

        activeSession.get().setEndDateTime(Instant.now());

        dao.update(activeSession.get());
    }

    @Override
    public List<WorkSession> findActiveSessionsByStartDateAndTimezoneOffset(final LocalDate startDate,
                                                                            final long timezoneOffset,
                                                                            final int limit) {
        return dao.findActiveSessionsByStartDateAndTimezoneOffset(startDate, timezoneOffset, limit);
    }

    @Override
    public User getWorkSessionOwner(final Integer workSessionId) {
        return Optional.ofNullable(dao.findOne(workSessionId))
                .map(WorkSession::getOwner)
                .orElseThrow(() -> new ServiceException("Can't find session with id " + workSessionId, ServiceException.Type.NOT_FOUND));
    }

    @Override
    public void deleteSession(final Integer workSessionId) {
        final WorkSession session = Optional.ofNullable(dao.findOne(workSessionId))
                .orElseThrow(() -> new ServiceException("Can't find session with id " + workSessionId, ServiceException.Type.NOT_FOUND));
        dao.delete(session);
    }

    private static LocalDate getCurrentLocalDate(final ZoneId zoneId) {
        return Instant.now().atZone(zoneId).toLocalDate();
    }
}
