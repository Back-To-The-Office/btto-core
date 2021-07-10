package com.btto.core.controller.model;

import lombok.Value;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

@Value
public class CreateOrUpdateRoomRequest {
    @Nullable
    String name;
    @Nullable
    String level;
    @NotNull
    Integer officeId;
}
