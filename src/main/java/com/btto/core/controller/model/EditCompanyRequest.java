package com.btto.core.controller.model;

import lombok.Data;

import javax.annotation.Nullable;
import javax.validation.constraints.Size;

/**
 * json example
 *     "name":"btto",
 */
@Data
public class EditCompanyRequest {
    @Nullable
    @Size(max = 255)
    private String name;
}
