package com.btto.core.controller.model;

import lombok.Data;

import javax.annotation.Nullable;
import javax.validation.constraints.Size;

@Data
public class EditDepartmentRequest {
    @Nullable
    @Size(min = 1, max = 255)
    private String name;
}
