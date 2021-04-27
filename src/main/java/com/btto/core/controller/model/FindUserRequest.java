package com.btto.core.controller.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class FindUserRequest {
    @NotNull
    @Size(min = 3, max = 255)
    private String email;
}
