package com.btto.core.controller;

import com.btto.core.controller.model.CompanyUsersResponse;
import com.btto.core.controller.model.CreateEntityResponse;
import com.btto.core.controller.model.CreateUserRequest;
import com.btto.core.controller.model.EditUserRequest;
import com.btto.core.controller.model.RegisterUserRequest;
import com.btto.core.controller.model.UserResponse;
import com.btto.core.domain.Company;
import com.btto.core.domain.User;
import com.btto.core.domain.enums.Role;
import com.btto.core.service.AccessService;
import com.btto.core.service.UserService;
import com.btto.core.spring.CurrentUser;
import com.google.common.collect.ImmutableList;
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
        if (userService.isExists(request.getEmail())) {
            throw new ApiException("User with email " + request.getEmail() + " already exists", HttpStatus.CONFLICT);
        }
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

    @GetMapping("/users")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public CompanyUsersResponse getUsers(@ApiIgnore @CurrentUser final User currentUser) {
        // we should request user data again to initialize proxy
        final User user = userService.find(currentUser.getId()).orElseThrow(
            () -> new ApiException("User has been deleted", HttpStatus.NOT_FOUND));

        final Company company = user.getCompany().orElseThrow(() -> new ApiException("User without company can't create a user"));
        if (!accessService.hasCompanyRight(user, company.getId(), AccessService.CompanyRight.VIEW)) {
            throw new ApiException("User " + user.getId() + " doesn't have enough rights to view the company", HttpStatus.FORBIDDEN);
        }

        return new CompanyUsersResponse(company.getUsers().stream()
            .map(CompanyUsersResponse.User::fromUser)
            .collect(ImmutableList.toImmutableList()));
    }

    @GetMapping("/users/current")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getCurrent(@ApiIgnore @CurrentUser final User user) {
        if (user == null) {
            throw new ApiException("No current user", HttpStatus.FORBIDDEN);
        }
        // we should request user data again to initialize departments proxy
        final User currentUser = userService.find(user.getId()).orElseThrow(
            () -> new ApiException("User has been deleted", HttpStatus.NOT_FOUND));
        return UserResponse.fromUserDomain(currentUser);
    }

    @PostMapping("/users/create")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.CREATED)
    public CreateEntityResponse create(@ApiIgnore @CurrentUser User currentUser, @Valid @RequestBody CreateUserRequest request) {
        if (!accessService.hasUserRight(currentUser, null, AccessService.UserRight.CREATE)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to create a user", HttpStatus.FORBIDDEN);
        }
        return new CreateEntityResponse(userService.create(
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                request.getRole().getDomainRole(),
                currentUser.getCompany().orElseThrow(() -> new ApiException("User without company can't create a user")),
                request.getTimezone(),
                request.getPosition()));
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
