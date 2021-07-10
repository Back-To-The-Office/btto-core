package com.btto.core.controller.model;

import lombok.Value;

import javax.annotation.Nullable;

@Value
public class CreateOrUpdateDeskRequest {
    @Nullable
    String name;
    int capacity;
    int roomId;
}
