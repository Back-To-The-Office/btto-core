package com.btto.core.controller.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * json example
 *     "name":"btto",
 */
@Data
public class CreateCompanyRequest {
    @NotNull
    @Size(max = 255)
    private String name;
}
