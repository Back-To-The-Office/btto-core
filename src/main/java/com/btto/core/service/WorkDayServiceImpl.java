package com.btto.core.service;

import com.btto.core.dao.WorkDayDao;
import com.btto.core.domain.User;
import com.btto.core.domain.WorkDay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class WorkDayServiceImpl extends AbstractEntityServiceImpl<WorkDay, WorkDayDao> implements WorkDayService {
    @Autowired
    public WorkDayServiceImpl(WorkDayDao dao) {
        super(dao);
    }

    @Override
    public boolean createOrGetExisting(final User owner, final Instant startDate) {
        // try to get previous records
        final LocalDate currentDay = startDate.atZone(owner.getTimezone()).toLocalDate();
        final Optional<WorkDay> lastWorkDay = dao.findLastWorkDay(owner, currentDay);
        // if exists, check that it had been closed
        if (lastWorkDay.isPresent() && lastWorkDay.get().getEndDateTime() != null) {
            return false;
        }
        final WorkDay newWorkDate = new WorkDay();
        newWorkDate.setDaySequenceNum(lastWorkDay.map(day -> day.getDaySequenceNum() + 1).orElse(0));
        newWorkDate.setOwner(owner);
        final long offsetInMinutes =TimeUnit.SECONDS.toMinutes(
                owner.getTimezone().getRules().getOffset(Instant.now()).get(ChronoField.MINUTE_OF_DAY)
        );
        newWorkDate.setTimezoneOffset(offsetInMinutes);
        // create new record
        dao.create(newWorkDate);
        return true;
    }

        /*
        void create(String email, String password, String firstName, String lastName, Role role, @Nullable Company company,
                @Nullable ZoneId timezone, @Nullable String position);

    Optional<User> findUserByEmail(String email);

    void deactivateUser(Integer userId);

    User update(Integer userId, @Nullable String oldPassword, @Nullable String newPassword, @Nullable String firstName,
                @Nullable String lastName, @Nullable ZoneId timezone, @Nullable Role role, @Nullable final String position);
     */
}
