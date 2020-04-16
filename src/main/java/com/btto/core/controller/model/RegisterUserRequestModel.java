package com.btto.core.controller.model;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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
public class RegisterUserRequestModel {
    @NotNull
    @Min(3)
    @Max(255)
    private String email;
    @NotNull
    @Max(255)
    private String firstName;
    @Max(255)
    private String lastName;
    @NotNull
    private String password;
    private ZoneId timezone;
    @Max(255)
    private String position;
}
