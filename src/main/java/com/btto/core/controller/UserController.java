package com.btto.core.controller;

import com.btto.core.controller.model.RegisterUserRequest;
import com.btto.core.controller.model.UserModel;
import com.btto.core.domain.enums.Role;
import com.btto.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController extends ApiV1AbstractController {

    private final UserService userService;

    public UserController(@Autowired UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public UserModel register(@Valid @RequestBody final RegisterUserRequest request) {
        userService.create(
                request.getEmail(), request.getPassword(), request.getFirstName(), request. getLastName(), Role.Admin
        );
        return UserModel.fromUserDomain(userService.findUserByEmail(request.getEmail()).orElseThrow(() -> new ApiException("Unexpected error during user registration")));
    }
}
