package com.btto.core.controller.model;

import lombok.Data;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

@Data
public class CreateOrUpdateRoomRequest {
    @Nullable
    private String name;
    @Nullable
    private String level;
    @NotNull
    private Integer officeId;
}
