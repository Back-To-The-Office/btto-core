package com.btto.core.controller.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZoneId;

/**
 * json example
 *     "email":"test@test.com",
 *     "firstName":"Sergey",
 *     "lastName":"Efimov",
 *     "password":"1234",
 *     "timezone":"UTC+3",
 *     "position":"back-end developer"
 */
@Data
public class RegisterUserRequest {
    @NotNull
    @Size(min = 3, max = 255)
    private String email;
    @NotNull
    @Size(max = 255)
    private String firstName;
    @Size(max = 255)
    private String lastName;
    @NotNull
    private String password;
    private ZoneId timezone;
    @Size(max = 255)
    private String position;
}
