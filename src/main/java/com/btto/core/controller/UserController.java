package com.btto.core.controller;

import com.btto.core.controller.model.CreateUserRequest;
import com.btto.core.controller.model.EditUserRequest;
import com.btto.core.controller.model.RegisterUserRequest;
import com.btto.core.controller.model.UserResponse;
import com.btto.core.domain.User;
import com.btto.core.domain.enums.Role;
import com.btto.core.service.AccessService;
import com.btto.core.service.UserService;
import com.btto.core.spring.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;


import javax.validation.Valid;

@RestController
public class UserController extends ApiV1AbstractController {

    private final UserService userService;
    private final AccessService accessService;

    @Autowired
    public UserController(UserService userService, AccessService accessService) {
        this.userService = userService;
        this.accessService = accessService;
    }

    @PostMapping("/users/register")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody final RegisterUserRequest request) {
        userService.create(
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request. getLastName(),
                Role.Admin,
                null,
                request.getTimezone(),
                request.getPosition()
        );
        return UserResponse.fromUserDomain(
                userService.findUserByEmail(request
                        .getEmail())
                        .orElseThrow(() -> new ApiException("Unexpected error during user registration", HttpStatus.GONE))
        );
    }

    @GetMapping("/users/{userId}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public UserResponse get(@ApiIgnore @CurrentUser final User currentUser, @PathVariable Integer userId) {
        if (!accessService.hasUserRight(currentUser, userId, AccessService.UserRight.VIEW)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to view user with id " + userId, HttpStatus.FORBIDDEN);
        }
        return UserResponse.fromUserDomain(userService.find(userId)
                .orElseThrow(() -> new ApiException("Can't find user with id " + userId, HttpStatus.GONE)));
    }

    @GetMapping("/users/current")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getCurrent(@ApiIgnore @CurrentUser final User user) {
        if (user == null) {
            throw new ApiException("No current user", HttpStatus.FORBIDDEN);
        }
        return UserResponse.fromUserDomain(user);
    }

    @PostMapping("/users/create")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@ApiIgnore @CurrentUser User currentUser, @Valid @RequestBody CreateUserRequest request) {
        if (!accessService.hasUserRight(currentUser, null, AccessService.UserRight.CREATE)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to create a user", HttpStatus.FORBIDDEN);
        }
        userService.create(
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request. getLastName(),
                request.getRole().getDomainRole(),
                currentUser.getCompany().orElseThrow(() -> new ApiException("User without company can't create a user")),
                request.getTimezone(),
                request.getPosition()
        );
    }

    @DeleteMapping("/users/{userId}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@ApiIgnore @CurrentUser User currentUser, @PathVariable final Integer userId) {
        if (!accessService.hasUserRight(currentUser, userId, AccessService.UserRight.REMOVE)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to remove user with id " + userId, HttpStatus.FORBIDDEN);
        }
        userService.deactivateUser(userId);
    }

    @PostMapping("/users/edit/{userId}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public UserResponse edit(
            @ApiIgnore @CurrentUser final User currentUser,
            @PathVariable final Integer userId,
            @Valid @RequestBody final EditUserRequest request
    ) {
        if (!accessService.hasUserRight(currentUser, userId, AccessService.UserRight.EDIT)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to remove user with id " + userId, HttpStatus.FORBIDDEN);
        }

        return UserResponse.fromUserDomain(userService.update(
                userId,
                request.getOldPassword(),
                request.getNewPassword(),
                request.getFirstName(),
                request.getLastName(),
                request.getTimezone(),
                request.getDomainRole(),
                request.getPosition()
        ));
    }
}
