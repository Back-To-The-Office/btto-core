package com.btto.core.controller.model;

import lombok.Data;

import javax.annotation.Nullable;

@Data
public class CreateOrUpdateDeskRequest {
    @Nullable
    private String name;
    private int capacity;
    private int roomId;
}
