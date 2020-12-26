package com.btto.core.controller.model;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class CreateWorkSessionRequest {
    @NotNull
    Integer ownerId;
}
