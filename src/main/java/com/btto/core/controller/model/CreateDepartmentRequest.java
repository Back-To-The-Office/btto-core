package com.btto.core.controller.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreateDepartmentRequest {
    @NotNull
    @Size(min = 1, max = 255)
    private String name;
}
