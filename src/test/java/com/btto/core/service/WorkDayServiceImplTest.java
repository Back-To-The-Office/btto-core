package com.btto.core.service;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.concurrent.TimeUnit;

class WorkDayServiceImplTest {
    @Test
    public void testDeleteMe() {
        System.out.println("Minute offset " +
                TimeUnit.SECONDS.toMinutes(ZoneId.systemDefault().getRules().getOffset(Instant.now()).getTotalSeconds()));
    }
}
