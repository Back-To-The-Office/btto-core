package com.btto.core.controller;

import com.btto.core.controller.model.CompanyResponse;
import com.btto.core.controller.model.CompanyUsersResponse;
import com.btto.core.controller.model.CreateCompanyRequest;
import com.btto.core.controller.model.CreateEntityResponse;
import com.btto.core.controller.model.EditCompanyRequest;
import com.btto.core.domain.Company;
import com.btto.core.domain.User;
import com.btto.core.service.AccessService;
import com.btto.core.service.CompanyService;
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
public class CompanyController extends ApiV1AbstractController {

    private final AccessService accessService;
    private final CompanyService companyService;

    @Autowired
    public CompanyController(final AccessService accessService,
                             final CompanyService companyService) {
        this.accessService = accessService;
        this.companyService = companyService;
    }

    @GetMapping("/companies/{companyId}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public CompanyResponse get(@ApiIgnore @CurrentUser final User currentUser, @PathVariable final Integer companyId) {
        if (!accessService.hasCompanyRight(currentUser, companyId, AccessService.CompanyRight.EDIT)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to edit the company", HttpStatus.FORBIDDEN);
        }

        return CompanyResponse.fromCompanyDomain(companyService.find(companyId)
                .orElseThrow(() -> new ApiException("Can't find the company", HttpStatus.NOT_FOUND)));
    }

    @GetMapping("/companies/{companyId}/users")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public CompanyUsersResponse getUsers(@ApiIgnore @CurrentUser final User currentUser, @PathVariable final Integer companyId) {
        if (!accessService.hasCompanyRight(currentUser, companyId, AccessService.CompanyRight.VIEW)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to view the company", HttpStatus.FORBIDDEN);
        }

        final Company company = companyService.find(companyId)
            .orElseThrow(() -> new ApiException("Can't find the company", HttpStatus.NOT_FOUND));

        return new CompanyUsersResponse(company.getUsers().stream()
            .map(CompanyUsersResponse.User::fromUser)
            .collect(ImmutableList.toImmutableList()));
    }

    @PostMapping("/companies/create")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.CREATED)
    public CreateEntityResponse create(@ApiIgnore @CurrentUser final User currentUser, @Valid @RequestBody final CreateCompanyRequest request) {
        if (!accessService.hasCompanyRight(currentUser, null, AccessService.CompanyRight.CREATE)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to create a company", HttpStatus.FORBIDDEN);
        }
        return new CreateEntityResponse(companyService.create(request.getName(), currentUser));
    }

    @DeleteMapping("/companies/{companyId}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@ApiIgnore @CurrentUser final User currentUser, @PathVariable final Integer companyId) {
        if (!accessService.hasCompanyRight(currentUser, companyId, AccessService.CompanyRight.REMOVE)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to delete the company", HttpStatus.FORBIDDEN);
        }
        companyService.delete(companyId);
    }

    @PostMapping("/companies/edit/{companyId}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public CompanyResponse edit(@ApiIgnore @CurrentUser final User currentUser, @PathVariable final Integer companyId, @Valid @RequestBody final EditCompanyRequest request) {
        if (!accessService.hasCompanyRight(currentUser, companyId, AccessService.CompanyRight.EDIT)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to edit the company", HttpStatus.FORBIDDEN);
        }

        return CompanyResponse.fromCompanyDomain(companyService.update(companyId, request.getName()));
    }
}
