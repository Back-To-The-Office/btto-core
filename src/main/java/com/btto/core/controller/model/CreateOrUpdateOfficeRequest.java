package com.btto.core.controller.model;

import lombok.Data;

import javax.annotation.Nullable;

@Data
public class CreateOrUpdateOfficeRequest {
    @Nullable
    private String name;
    @Nullable
    private String address;
}
