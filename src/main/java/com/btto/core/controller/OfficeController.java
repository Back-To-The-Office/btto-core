package com.btto.core.controller;

import com.btto.core.controller.model.CreateEntityResponse;
import com.btto.core.controller.model.CreateOrUpdateOfficeRequest;
import com.btto.core.controller.model.OfficeModel;
import com.btto.core.domain.Company;
import com.btto.core.domain.User;
import com.btto.core.service.AccessService;
import com.btto.core.service.OfficeService;
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
public class OfficeController extends ApiV1AbstractController {

    private final AccessService accessService;
    private final OfficeService officeService;

    @Autowired
    public OfficeController(final AccessService accessService, final OfficeService officeService) {
        this.accessService = accessService;
        this.officeService = officeService;
    }

    @GetMapping("/office/all")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<OfficeModel> getAll(@ApiIgnore @CurrentUser final User currentUser) {
        final Company company = currentUser.getCompany().orElseThrow(
                () -> new ApiException("User without company can't view any offices. Please create company before.", HttpStatus.NOT_ACCEPTABLE));
        if (!accessService.hasOfficeRight(currentUser, null, AccessService.OfficeRight.VIEW)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to view offices.", HttpStatus.FORBIDDEN);
        }

        return officeService.getCompanyOffices(company).stream()
                .map(OfficeModel::fromOffice)
                .collect(ImmutableList.toImmutableList());
    }

    @PostMapping("/office/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CreateEntityResponse create(@ApiIgnore @CurrentUser final User currentUser, @RequestBody @Valid CreateOrUpdateOfficeRequest request) {
        final Company company = currentUser.getCompany().orElseThrow(
                () -> new ApiException("User without company can't create an office. Please create company before.", HttpStatus.NOT_ACCEPTABLE));
        if (!accessService.hasOfficeRight(currentUser, null, AccessService.OfficeRight.CREATE)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to create office.", HttpStatus.FORBIDDEN);
        }

        return new CreateEntityResponse(officeService.create(company, request.getName(), request.getAddress()).getId());
    }

    @PostMapping("/office/edit/{officeId}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OfficeModel edit(@ApiIgnore @CurrentUser final User currentUser, @PathVariable final Integer officeId, @RequestBody @Valid CreateOrUpdateOfficeRequest request) {
        if (!accessService.hasOfficeRight(currentUser, null, AccessService.OfficeRight.EDIT)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to edit office " + officeId, HttpStatus.FORBIDDEN);
        }

        return OfficeModel.fromOffice(officeService.update(officeId, request.getName(), request.getAddress()));
    }

    @DeleteMapping("/office/{officeId}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void delete(@ApiIgnore @CurrentUser final User currentUser, @PathVariable final Integer officeId) {
        if (!accessService.hasOfficeRight(currentUser, null, AccessService.OfficeRight.DELETE)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to delete office " + officeId, HttpStatus.FORBIDDEN);
        }

        officeService.delete(officeId);
    }
}
