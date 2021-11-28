package com.btto.core.controller.model;

import lombok.Data;

import java.time.Instant;

@Data
public class EditWorkSessionRequest {
    private Instant startTime;
    private Instant endTime;
}
