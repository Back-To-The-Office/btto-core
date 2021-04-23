package com.btto.core.service;

import com.btto.core.dao.WorkSessionDao;
import com.btto.core.domain.User;
import com.btto.core.domain.WorkSession;
import com.btto.core.mock.MockUserBuilder;
import com.btto.core.mock.MockWorkSessionBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WorkSessionServiceImplTest {
    final private WorkSessionDao workSessionDao = mock(WorkSessionDao.class);

    final private WorkSessionService workSessionService = new WorkSessionServiceImpl(workSessionDao);

    @Test
    public void testCreateIfNotExistsWhenActiveSessionExists() {
        final Integer userId = 1;
        final ZoneId userTimezone = ZoneId.of("Canada/Newfoundland");
        final User user = new MockUserBuilder(userId).timezone(userTimezone).build();
        final Instant currentInstance = Instant.now();
        final LocalDate currentDay = currentInstance.atZone(userTimezone).toLocalDate();

        final WorkSession existingSession = new MockWorkSessionBuilder()
                .owner(user)
                .sessionDate(currentInstance.atZone(userTimezone).toLocalDate())
                .startDateTime(currentInstance.minusSeconds(1))
                .endDateTime(currentInstance)
                .daySequenceNum(1)
                .build();

        when(workSessionDao.findLastWorkSession(eq(user), eq(currentDay))).thenReturn(Optional.of(existingSession));

        assertFalse(workSessionService.createActiveSession(user));
    }

    @Test
    @Disabled("It' doesn't check winter/summer time")
    public void testCreateIfNotExistsWhenActiveSessionNotExists() {
        final Integer userId = 1;
        final ZoneId userTimezone = ZoneId.of("Canada/Newfoundland");
        final User user = new MockUserBuilder(userId).timezone(userTimezone).build();
        final Instant currentInstance = Instant.now();
        final LocalDate currentDay = currentInstance.atZone(userTimezone).toLocalDate();
        final ArgumentCaptor<WorkSession> workSessionArgumentCaptor = ArgumentCaptor.forClass(WorkSession.class);

        when(workSessionDao.findLastWorkSession(eq(user), eq(currentDay))).thenReturn(Optional.empty());

        assertTrue(workSessionService.createActiveSession(user));

        verify(workSessionDao, times(1)).create(workSessionArgumentCaptor.capture());

        final WorkSession actualSession = workSessionArgumentCaptor.getValue();
        assertNotNull(actualSession);

        assertEquals(-210L, actualSession.getTimezoneOffset());
    }

    @Test
    public void testEditSessionWhenSessionNotExists() {
        final Instant currentTime = Instant.now();
        try {
            workSessionService.editSession(1, currentTime, currentTime.plusSeconds(60));
            fail();
        } catch (final ServiceException e) {
            assertEquals(e.getType(), ServiceException.Type.NOT_FOUND);
        }
    }

    @Test
    public void testEditSessionWhenSessionExists() {
        final Instant currentTime = Instant.now();
        final int id = 1;
        final Integer userId = 1;
        final ZoneId userTimezone = ZoneId.of("Canada/Newfoundland");
        final User user = new MockUserBuilder(userId).timezone(userTimezone).build();
        final WorkSession existingSession = new MockWorkSessionBuilder()
                .id(id)
                .owner(user)
                .build();

        final ArgumentCaptor<WorkSession> workSessionArgumentCaptor = ArgumentCaptor.forClass(WorkSession.class);

        when(workSessionDao.findOne(eq(id))).thenReturn(existingSession);

        workSessionService.editSession(id, currentTime, currentTime.plusSeconds(60));

        verify(workSessionDao, times(1)).update(workSessionArgumentCaptor.capture());

        final WorkSession actualSession = workSessionArgumentCaptor.getValue();
        assertNotNull(actualSession);

        assertEquals(user, actualSession.getOwner());
    }

    @Test
    public void testCloseActiveSessionWhenSessionNotExists() {
        final Integer userId = 1;
        final User user = new MockUserBuilder(userId).build();
        try {
            workSessionService.closeActiveSession(user);
            fail();
        } catch (final ServiceException e) {
            assertEquals(e.getType(), ServiceException.Type.NOT_FOUND);
        }
    }

    @Test
    public void testCloseActiveSessionWhenSessionExists() {
        final int id = 1;
        final Integer userId = 1;
        final User user = new MockUserBuilder(userId).build();
        final WorkSession existingSession = new WorkSession();

        existingSession.setId(id);
        existingSession.setOwner(user);

        final ArgumentCaptor<WorkSession> workSessionArgumentCaptor = ArgumentCaptor.forClass(WorkSession.class);

        when(workSessionDao.findLastActiveWorkSession(eq(user))).thenReturn(Optional.of(existingSession));

        workSessionService.closeActiveSession(user);

        verify(workSessionDao, times(1)).update(workSessionArgumentCaptor.capture());

        final WorkSession actualSession = workSessionArgumentCaptor.getValue();
        assertNotNull(actualSession);

        assertEquals(user, actualSession.getOwner());
        assertNotNull(actualSession.getEndDateTime());
    }
}
