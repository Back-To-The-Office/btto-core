package com.btto.core.controller;

import com.btto.core.controller.model.AddParticipantToDepartmentRequest;
import com.btto.core.controller.model.AssignDepartmentRequest;
import com.btto.core.controller.model.CreateDepartmentRequest;
import com.btto.core.controller.model.CreateEntityResponse;
import com.btto.core.controller.model.DepartmentResponse;
import com.btto.core.controller.model.EditDepartmentRequest;
import com.btto.core.domain.User;
import com.btto.core.service.AccessService;
import com.btto.core.service.DepartmentService;
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
import java.util.List;

@RestController
public class DepartmentController extends ApiV1AbstractController {

    private final AccessService accessService;
    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(AccessService accessService, DepartmentService companyService) {
        this.accessService = accessService;
        this.departmentService = companyService;
    }

    @GetMapping("/departments/{departmentId}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public DepartmentResponse get(@ApiIgnore @CurrentUser final User currentUser, @PathVariable final Integer departmentId) {
        if (!accessService.hasDepartmentRight(currentUser, departmentId, AccessService.DepartmentRight.VIEW)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to view a department", HttpStatus.FORBIDDEN);
        }

        return DepartmentResponse.fromDepartmentDomain(departmentService.find(departmentId).orElseThrow(
                () -> new ApiException("Can't department with id " + departmentId, HttpStatus.GONE)
        ));
    }

    @GetMapping("/departments")
    @Transactional//(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public List<DepartmentResponse> getAllDepartments(@ApiIgnore @CurrentUser final User currentUser) {
        if (!accessService.hasDepartmentRight(currentUser, null, AccessService.DepartmentRight.VIEW_ALL)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to view departments", HttpStatus.FORBIDDEN);
        }

        return departmentService.getUserCompanyDepartments(currentUser).stream()
            .map(DepartmentResponse::fromDepartmentDomain)
            .collect(ImmutableList.toImmutableList());
    }

    @PostMapping("/departments/create")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.CREATED)
    public CreateEntityResponse create(@ApiIgnore @CurrentUser final User currentUser, @Valid @RequestBody CreateDepartmentRequest request) {
        if (!accessService.hasDepartmentRight(currentUser, null, AccessService.DepartmentRight.CREATE)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to create a department", HttpStatus.FORBIDDEN);
        }
        return new CreateEntityResponse(departmentService.create(request.getName(), currentUser));
    }

    @DeleteMapping("/departments/{departmentId}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@ApiIgnore @CurrentUser User currentUser, @PathVariable final Integer departmentId) {
        if (!accessService.hasDepartmentRight(currentUser, departmentId, AccessService.DepartmentRight.REMOVE)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to delete department with id " + departmentId, HttpStatus.FORBIDDEN);
        }
        departmentService.delete(departmentId);
    }

    @PostMapping("/departments/edit/{departmentId}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public DepartmentResponse edit(@ApiIgnore @CurrentUser final User currentUser,
                                   @PathVariable final Integer departmentId,
                                   @Valid @RequestBody final EditDepartmentRequest request) {
        if (!accessService.hasDepartmentRight(currentUser, departmentId, AccessService.DepartmentRight.EDIT)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to edit department with id " + departmentId, HttpStatus.FORBIDDEN);
        }

        return DepartmentResponse.fromDepartmentDomain(departmentService.update(departmentId, request.getName()));
    }

    @PostMapping("/departments/assign/{departmentId}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public DepartmentResponse assign(@ApiIgnore @CurrentUser final User currentUser,
                                     @PathVariable final Integer departmentId,
                                     @Valid @RequestBody final AssignDepartmentRequest request) {
        if (!accessService.hasDepartmentRight(currentUser, departmentId, AccessService.DepartmentRight.ASSIGN)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to assign owner to department with id " + departmentId, HttpStatus.FORBIDDEN);
        }

        return DepartmentResponse.fromDepartmentDomain(departmentService.assign(departmentId, request.getOwnerId()));
    }

    @PostMapping("/departments/{departmentId}/add/")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public void addParticipant(@ApiIgnore @CurrentUser final User currentUser,
                               @PathVariable final Integer departmentId,
                               @Valid @RequestBody final AddParticipantToDepartmentRequest request) {
        if (accessService.hasDepartmentRight(currentUser, departmentId, AccessService.DepartmentRight.ADD_PARTICIPANT)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to add participants to department with id " + departmentId, HttpStatus.FORBIDDEN);
        }
        if (accessService.isUserCanBeAddedToDepartment(request.getParticipantId(), departmentId)) {
            throw new ApiException("User " + request.getParticipantId() + " can't be added to department with id " + departmentId, HttpStatus.FORBIDDEN);
        }

        departmentService.addParticipant(departmentId, request.getParticipantId());
    }

    @PostMapping("/departments/{departmentId}/remove/")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public void removeParticipant(@ApiIgnore @CurrentUser final User currentUser,
                               @PathVariable final Integer departmentId,
                               @Valid @RequestBody final AddParticipantToDepartmentRequest request) {
        if (accessService.hasDepartmentRight(currentUser, departmentId, AccessService.DepartmentRight.ADD_PARTICIPANT)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to remove participants to department with id " + departmentId, HttpStatus.FORBIDDEN);
        }

        if (accessService.isUserCanBeRemovedFromDepartment(request.getParticipantId(), departmentId)) {
            throw new ApiException("User " + request.getParticipantId() + " can't be removed from department with id " + departmentId, HttpStatus.FORBIDDEN);
        }

        departmentService.removeParticipant(departmentId, request.getParticipantId());
    }

}
