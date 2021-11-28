package com.btto.core.controller.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateWorkSessionRequest {
    @NotNull
    private Integer ownerId;
}
