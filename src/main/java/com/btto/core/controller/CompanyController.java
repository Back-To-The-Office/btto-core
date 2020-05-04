package com.btto.core.controller;

import com.btto.core.controller.model.CompanyResponse;
import com.btto.core.controller.model.CreateCompanyRequest;
import com.btto.core.controller.model.EditCompanyRequest;
import com.btto.core.domain.User;
import com.btto.core.service.AccessService;
import com.btto.core.service.CompanyService;
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

    @PostMapping("/companies/create")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@CurrentUser final User currentUser, @Valid @RequestBody final CreateCompanyRequest request) {
        if (!accessService.hasCompanyRight(currentUser, null, AccessService.CompanyRight.CREATE)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to create a company", HttpStatus.FORBIDDEN);
        }
        companyService.create(request.getName(), currentUser);
    }

    @DeleteMapping("/companies/{companyId}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@CurrentUser final User currentUser, @PathVariable final Integer companyId) {
        if (!accessService.hasCompanyRight(currentUser, companyId, AccessService.CompanyRight.REMOVE)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to delete the company", HttpStatus.FORBIDDEN);
        }
        companyService.delete(companyId);
    }

    @PostMapping("/companies/edit/{companyId}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public CompanyResponse edit(@CurrentUser final User currentUser, @PathVariable final Integer companyId, @Valid @RequestBody final EditCompanyRequest request) {
        if (!accessService.hasCompanyRight(currentUser, companyId, AccessService.CompanyRight.EDIT)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to edit the company", HttpStatus.FORBIDDEN);
        }

        return CompanyResponse.fromCompanyDomain(companyService.update(companyId, request.getName()));
    }

    @GetMapping("/companies/{companyId}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ResponseStatus(HttpStatus.OK)
    public CompanyResponse view(@CurrentUser final User currentUser, @PathVariable final Integer companyId) {
        if (!accessService.hasCompanyRight(currentUser, companyId, AccessService.CompanyRight.EDIT)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to edit the company", HttpStatus.FORBIDDEN);
        }

        return CompanyResponse.fromCompanyDomain(companyService.find(companyId)
                .orElseThrow(() -> new ApiException("Can't find the company", HttpStatus.NOT_FOUND)));
    }
}
