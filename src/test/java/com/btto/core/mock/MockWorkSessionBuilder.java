package com.btto.core.mock;

import com.btto.core.domain.User;
import com.btto.core.domain.WorkSession;

import java.time.Instant;
import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unused")
public class MockWorkSessionBuilder {

    private Integer id;
    private User owner;
    private LocalDate sessionDate;
    private Instant startDateTime;
    private Instant endDateTime;
    private Long timezoneOffset;
    private Integer daySequenceNum;

    public MockWorkSessionBuilder id(final int id) {
        this.id = id;
        return this;
    }

    public MockWorkSessionBuilder owner(final User owner) {
        this.owner = owner;
        return this;
    }

    public MockWorkSessionBuilder sessionDate(final LocalDate startDate) {
        this.sessionDate = startDate;
        return this;
    }

    public MockWorkSessionBuilder startDateTime(final Instant startDateTime) {
        this.startDateTime = startDateTime;
        return this;
    }

    public MockWorkSessionBuilder endDateTime(final Instant endDateTime) {
        this.endDateTime = endDateTime;
        return this;
    }

    public MockWorkSessionBuilder timezoneOffset(final long timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
        return this;
    }

    public MockWorkSessionBuilder daySequenceNum(final int daySequenceNum) {
        this.daySequenceNum = daySequenceNum;
        return this;
    }

    public WorkSession build() {
        final WorkSession workSession = mock(WorkSession.class);
        if (id != null) {
            when(workSession.getId()).thenReturn(id);
        }
        if (owner != null) {
            when(workSession.getOwner()).thenReturn(owner);
        }
        if (sessionDate != null) {
            when(workSession.getSessionDate()).thenReturn(sessionDate);
        }
        if (startDateTime != null) {
            when(workSession.getStartDateTime()).thenReturn(startDateTime);
        }
        if (endDateTime != null) {
            when(workSession.getEndDateTime()).thenReturn(endDateTime);
        }
        if (timezoneOffset != null) {
            when(workSession.getTimezoneOffset()).thenReturn(timezoneOffset);
        }
        if (daySequenceNum != null) {
            when(workSession.getDaySequenceNum()).thenReturn(daySequenceNum);
        }

        return workSession;
    }
}
