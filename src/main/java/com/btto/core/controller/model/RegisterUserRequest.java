package com.btto.core.controller.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * json example
 *     "email":"test@test.com"
 *     "firstName":"Sergey"
 *     "lastName":"Efimov"
 *     "password":"1234"
 */
@Data
public class RegisterUserRequest {
    @NotNull
    private String email;
    @NotNull
    private String firstName;
    private String lastName;
    @NotNull
    private String password;
}
