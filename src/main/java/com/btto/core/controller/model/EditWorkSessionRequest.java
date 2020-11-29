package com.btto.core.controller.model;

import lombok.Value;

import java.time.Instant;

@Value
public class EditWorkSessionRequest {
    Instant startTime;
    Instant endTime;
}
