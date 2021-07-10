package com.btto.core.controller;

import com.btto.core.controller.model.CreateEntityResponse;
import com.btto.core.controller.model.CreateOrUpdateDeskRequest;
import com.btto.core.controller.model.DeskModel;
import com.btto.core.domain.User;
import com.btto.core.service.AccessService;
import com.btto.core.service.DeskService;
import com.btto.core.spring.CurrentUser;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class DeskController extends ApiV1AbstractController {

    private final AccessService accessService;
    private final DeskService deskService;

    @GetMapping("/desk/{deskId}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DeskModel get(@ApiIgnore @CurrentUser final User currentUser, @PathVariable final Integer deskId) {
        if (!accessService.hasDeskRight(currentUser, deskId, AccessService.DeskRight.VIEW)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to view desk.", HttpStatus.FORBIDDEN);
        }
        return DeskModel.fromDesk(deskService.find(deskId)
                .orElseThrow(() -> new ApiException("Can't find desk with id " + deskId, HttpStatus.NOT_FOUND)));
    }

    @PostMapping("/desk/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CreateEntityResponse create(@ApiIgnore @CurrentUser final User currentUser, @RequestBody @Valid CreateOrUpdateDeskRequest request) {
        if (!accessService.hasDeskRight(currentUser, null, AccessService.DeskRight.CREATE)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to create desk.", HttpStatus.FORBIDDEN);
        }

        return new CreateEntityResponse(deskService.create(request.getRoomId(), request.getName(), request.getCapacity()).getId());
    }

    @PostMapping("/desk/edit/{deskId}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DeskModel edit(@ApiIgnore @CurrentUser final User currentUser, @PathVariable final Integer deskId, @RequestBody @Valid CreateOrUpdateDeskRequest request) {
        if (!accessService.hasDeskRight(currentUser, deskId, AccessService.DeskRight.EDIT)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to edit desk " + deskId, HttpStatus.FORBIDDEN);
        }

        return DeskModel.fromDesk(deskService.update(deskId, request.getName(), request.getCapacity()));
    }

    @DeleteMapping("/desk/{deskId}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void delete(@ApiIgnore @CurrentUser final User currentUser, @PathVariable final Integer deskId) {
        if (!accessService.hasDeskRight(currentUser, deskId, AccessService.DeskRight.DELETE)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to delete desk " + deskId, HttpStatus.FORBIDDEN);
        }

        deskService.delete(deskId);
    }
}
