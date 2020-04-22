package com.btto.core.controller.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * json example
 *     "email":"test@test.com",
 *     "firstName":"Sergey",
 *     "lastName":"Efimov",
 *     "password":"1234",
 *     "timezone":"UTC+3",
 *     "role":"USER"
 *     "position":"back-end developer"
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CreateUserRequest extends RegisterUserRequest {
    @NotNull
    private RoleModel role;
}
