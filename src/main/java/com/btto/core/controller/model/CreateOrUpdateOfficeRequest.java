package com.btto.core.controller.model;

import lombok.Value;

import javax.annotation.Nullable;

@Value
public class CreateOrUpdateOfficeRequest {
    @Nullable
    String name;
    @Nullable
    String address;
}
