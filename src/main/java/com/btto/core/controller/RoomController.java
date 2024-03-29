package com.btto.core.controller;

import com.btto.core.controller.model.CreateEntityResponse;
import com.btto.core.controller.model.CreateOrUpdateRoomRequest;
import com.btto.core.controller.model.DeskModel;
import com.btto.core.controller.model.RoomModel;
import com.btto.core.domain.Desk;
import com.btto.core.domain.User;
import com.btto.core.service.AccessService;
import com.btto.core.service.DeskService;
import com.btto.core.service.RoomService;
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
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class RoomController extends ApiV1AbstractController {

    private final AccessService accessService;
    private final RoomService roomService;
    private final DeskService deskService;


    @GetMapping("/room/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RoomModel get(@ApiIgnore @CurrentUser final User currentUser, @PathVariable final Integer roomId) {
        if (!accessService.hasRoomRight(currentUser, roomId, AccessService.RoomRight.VIEW)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to view room.", HttpStatus.FORBIDDEN);
        }
        return RoomModel.fromRoom(roomService.find(roomId)
                .orElseThrow(() -> new ApiException("Can't find room with id " + roomId, HttpStatus.NOT_FOUND)));
    }

    @GetMapping("/room/{roomId}/desks")
    @ResponseStatus(HttpStatus.OK)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<DeskModel> getDesks(@ApiIgnore @CurrentUser final User currentUser, @PathVariable final Integer roomId) {
        if (!accessService.hasRoomRight(currentUser, roomId, AccessService.RoomRight.VIEW)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to view room.", HttpStatus.FORBIDDEN);
        }

        List<Desk> desks = deskService.finalAllDesksOfRoom(roomId);

        return desks.stream()
                .map(DeskModel::fromDesk)
                .collect(Collectors.toList());
    }

    @PostMapping("/room/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CreateEntityResponse create(@ApiIgnore @CurrentUser final User currentUser, @RequestBody @Valid CreateOrUpdateRoomRequest request) {
        if (!accessService.hasRoomRight(currentUser, null, AccessService.RoomRight.CREATE)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to create room.", HttpStatus.FORBIDDEN);
        }

        return new CreateEntityResponse(roomService.create(request.getOfficeId(), request.getName(), request.getLevel()).getId());
    }

    @PostMapping("/room/edit/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RoomModel edit(@ApiIgnore @CurrentUser final User currentUser, @PathVariable final Integer roomId, @RequestBody @Valid CreateOrUpdateRoomRequest request) {
        if (!accessService.hasRoomRight(currentUser, roomId, AccessService.RoomRight.EDIT)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to edit room " + roomId, HttpStatus.FORBIDDEN);
        }

        return RoomModel.fromRoom(roomService.update(roomId, request.getName(), request.getLevel()));
    }

    @DeleteMapping("/room/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void delete(@ApiIgnore @CurrentUser final User currentUser, @PathVariable final Integer roomId) {
        if (!accessService.hasRoomRight(currentUser, roomId, AccessService.RoomRight.DELETE)) {
            throw new ApiException("User " + currentUser.getId() + " doesn't have enough rights to delete room " + roomId, HttpStatus.FORBIDDEN);
        }

        roomService.delete(roomId);
    }

}
