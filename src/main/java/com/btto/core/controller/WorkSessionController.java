package com.btto.core.controller;

import com.btto.core.controller.model.CreateWorkSessionRequest;
import com.btto.core.controller.model.CreateWorkSessionResponse;
import com.btto.core.controller.model.EditWorkSessionRequest;
import com.btto.core.controller.model.WorkSessionResponse;
import com.btto.core.domain.User;
import com.btto.core.service.AccessService;
import com.btto.core.service.UserService;
import com.btto.core.service.WorkSessionService;
import com.btto.core.spring.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Controller
public class WorkSessionController extends ApiV1AbstractController {

    private final AccessService accessService;
    private final WorkSessionService workSessionService;
    private final UserService userService;

    @Autowired
    public WorkSessionController(AccessService accessService, WorkSessionService workSessionService, UserService userService) {
        this.accessService = accessService;
        this.workSessionService = workSessionService;
        this.userService = userService;
    }

    @PostMapping("/worksession/create")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.CREATED)
    public CreateWorkSessionResponse create(@ApiIgnore @CurrentUser User currentUser, @Valid @RequestBody CreateWorkSessionRequest request) {
        final User sessionOwner;

        if (request.getOwnerId() != null) {
            sessionOwner = userService.find(request.getOwnerId()).orElseThrow(
                    () -> new ApiException("Can't find user with id: " + currentUser.getId(), HttpStatus.NOT_FOUND));
        } else {
            sessionOwner = currentUser;
        }

        if (!accessService.hasWorkSessionRight(currentUser, sessionOwner.getId(), AccessService.WorkSessionRight.CREATE)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to create a work session", HttpStatus.FORBIDDEN);
        }

        return new CreateWorkSessionResponse(workSessionService.createActiveSession(sessionOwner));
    }

    @PostMapping("/worksession/edit/{sessionId}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public WorkSessionResponse edit(@ApiIgnore @CurrentUser User currentUser,
                                    @Valid @RequestBody EditWorkSessionRequest request,
                                    @PathVariable Integer sessionId) {

        final Integer ownerId = workSessionService.getWorkSessionOwner(sessionId).getId();

        if (!accessService.hasWorkSessionRight(currentUser, ownerId, AccessService.WorkSessionRight.EDIT)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to edit a work session", HttpStatus.FORBIDDEN);
        }

        return WorkSessionResponse.from(workSessionService.editSession(sessionId, request.getStartTime(), request.getEndTime()));
    }

    @PostMapping("/worksession/close")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public void close(@ApiIgnore @CurrentUser User currentUser) {

        if (!accessService.hasWorkSessionRight(currentUser, currentUser.getId(), AccessService.WorkSessionRight.EDIT)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to close a work session", HttpStatus.FORBIDDEN);
        }

        workSessionService.closeActiveSession(currentUser);
    }

    @DeleteMapping("/worksession/{sessionId}}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@ApiIgnore @CurrentUser User currentUser, @PathVariable Integer sessionId) {
        final Integer ownerId = workSessionService.getWorkSessionOwner(sessionId).getId();

        if (!accessService.hasWorkSessionRight(currentUser, ownerId, AccessService.WorkSessionRight.DELETE)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to delete a work session", HttpStatus.FORBIDDEN);
        }

        workSessionService.deleteSession(sessionId);
    }


}
