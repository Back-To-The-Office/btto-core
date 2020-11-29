package com.btto.core.controller.model;

import com.btto.core.domain.WorkSession;
import lombok.Value;

import java.time.Instant;

@Value
public class WorkSessionResponse {
    Integer id;
    Integer daySequence;
    Instant startTime;
    Instant endTime;

    public static WorkSessionResponse from(WorkSession session) {
        return new WorkSessionResponse(session.getId(), session.getDaySequenceNum(), session.getStartDateTime(), session.getEndDateTime());
    }
}
