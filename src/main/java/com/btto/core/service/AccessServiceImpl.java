package com.btto.core.service;

import com.btto.core.domain.Company;
import com.btto.core.domain.Department;
import com.btto.core.domain.Desk;
import com.btto.core.domain.Office;
import com.btto.core.domain.Room;
import com.btto.core.domain.User;
import com.btto.core.domain.enums.Role;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Service
@Log
public class AccessServiceImpl implements AccessService {

    private final UserService userService;
    private final DepartmentService departmentService;
    private final RelationService relationService;
    private final OfficeService officeService;
    private final RoomService roomService;
    private final DeskService deskService;

    public AccessServiceImpl(@Autowired UserService userService,
                             @Autowired DepartmentService departmentService,
                             @Autowired RelationService relationService,
                             @Autowired OfficeService officeService,
                             @Autowired RoomService roomService,
                             @Autowired DeskService deskService) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.relationService = relationService;
        this.officeService = officeService;
        this.roomService = roomService;
        this.deskService = deskService;
    }

    @Override
    @Transactional
    public boolean isAdmin(final User user) {
        checkNotNull(user);
        return user.getRole().equals(Role.Admin);
    }

    @Override
    @Transactional
    public boolean hasCompanyRight(final User currentUser, @Nullable final Integer companyId, final CompanyRight right) {
        switch (right) {
            case VIEW:
                checkArgument(companyId != null);
                if (currentUser.getCompany().isPresent()) {
                    final Company userCompany = currentUser.getCompany().get();
                    if (hasAdminRights(currentUser) || userCompany.isEnabled()) {
                        return currentUser.getCompany().get().getId().equals(companyId);
                    }
                }
                break;
            case EDIT:
            case REMOVE:
                checkArgument(companyId != null);
                if (currentUser.getCompany().isPresent()) {
                    return hasAdminRights(currentUser) && currentUser.getCompany().get().getId().equals(companyId);
                }
                break;
            case CREATE:
                return hasAdminRights(currentUser) && (!currentUser.getCompany().isPresent() || !currentUser.getCompany().get().isEnabled());
        }
        return false;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    @Transactional
    public boolean hasUserRight(final User currentUser, @Nullable final Integer userId, final UserRight right) {
        switch (right) {
            case GET_STATUS:
            case VIEW: {
                checkArgument(userId != null);
                if (currentUser.getId().equals(userId)) {
                    return true;
                }
                if (!currentUser.getCompany().isPresent() || !currentUser.getCompany().get().isEnabled()) {
                    return false;
                }
                final Integer currentUserCompanyId = currentUser.getCompany().get().getId();
                final User subject = getUser(userId);
                if (subject.getCompany().isPresent()) {
                    return currentUserCompanyId.equals(subject.getCompany().get().getId());
                }
            } break;
            case EDIT: {
                checkArgument(userId != null);
                if (currentUser.getId().equals(userId)) {
                    return true;
                }
                if (!currentUser.getCompany().isPresent() || !currentUser.getCompany().get().isEnabled()) {
                    return false;
                }
                final Integer currentUserCompanyId = currentUser.getCompany().get().getId();
                final User subject = getUser(userId);
                if (subject.getCompany().isPresent()) {
                    return hasAdminRights(currentUser) && currentUserCompanyId.equals(subject.getCompany().get().getId());
                }
            } break;
            case REMOVE: {
                checkArgument(userId != null);
                if (currentUser.getId().equals(userId) && hasAdminRights(currentUser)) {
                    return true;
                }
                if (!currentUser.getCompany().isPresent() || !currentUser.getCompany().get().isEnabled()) {
                    return false;
                }
                final Integer currentUserCompanyId = currentUser.getCompany().get().getId();
                final User subject = getUser(userId);
                if (subject.getCompany().isPresent()) {
                    return hasAdminRights(currentUser) && currentUserCompanyId.equals(subject.getCompany().get().getId());
                }
            } break;
            case CREATE:
                if (!currentUser.getCompany().isPresent() || !currentUser.getCompany().get().isEnabled()) {
                    return false;
                }
                return hasAdminRights(currentUser);
            case SET_STATUS:
                return currentUser.getId().equals(userId);
        }
        return false;
    }

    @Override
    @Transactional
    public boolean hasDepartmentRight(final User currentUser, @Nullable final Integer departmentId, final DepartmentRight departmentRight) {
        if (!currentUser.getCompany().isPresent() || !currentUser.getCompany().get().isEnabled()) {
            return false;
        }
        final Integer currentUserCompanyId = currentUser.getCompany().get().getId();

        switch (departmentRight) {
            case VIEW: {
                checkArgument(departmentId != null);
                return getDepartment(departmentId).getCompany().getId().equals(currentUserCompanyId);
            }
            case VIEW_ALL:
                return true;
            case EDIT:
            case REMOVE: {
                checkArgument(departmentId != null);
                final Department subject = getDepartment(departmentId);
                if (subject.getCompany().getId().equals(currentUserCompanyId)) {
                    if (hasAdminRights(currentUser)) {
                        return true;
                    } else if (hasManagerRights(currentUser) && subject.getOwner().isPresent()) {
                        return currentUser.getId().equals(subject.getOwner().get().getId());
                    }
                }
            } break;
            case CREATE:
                return hasManagerRights(currentUser);
            case ASSIGN: {
                checkArgument(departmentId != null);
                final Department subject = getDepartment(departmentId);
                if (subject.getCompany().getId().equals(currentUserCompanyId)) {
                    if (hasAdminRights(currentUser)) {
                        return true;
                    } else if (hasManagerRights(currentUser)) {
                        return !subject.getOwner().isPresent()
                                || subject.getOwner().get().getId().equals(currentUser.getId())
                                || relationService.isManager(currentUser, subject.getOwner().get());
                    }
                }
            } break;
            case ADD_PARTICIPANT: {
                checkArgument(departmentId != null);
                final Department subject = getDepartment(departmentId);
                if (subject.getCompany().getId().equals(currentUserCompanyId)) {
                    if (hasAdminRights(currentUser)) {
                        return true;
                    } else if (hasManagerRights(currentUser) && subject.getOwner().isPresent()) {
                        return subject.getOwner().get().getId().equals(currentUser.getId())
                                || relationService.isManager(currentUser, subject.getOwner().get());
                    }
                }
            } break;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean hasWorkSessionRight(final User currentUser, final Integer ownerId, final WorkSessionRight workSessionRight) {
        if (!currentUser.getCompany().isPresent() || !currentUser.getCompany().get().isEnabled()) {
            return false;
        }
        final User owner = getUser(ownerId);
        if (!owner.getCompany().isPresent() || !owner.getCompany().get().getId().equals(currentUser.getCompany().get().getId())) {
            return false;
        }

        switch (workSessionRight) {
            case CREATE:
            case VIEW:
            case DELETE:
            case EDIT:
                if (hasAdminRights(currentUser) || currentUser.getId().equals(ownerId) || relationService.isManager(currentUser, owner)) {
                    return true;
                }
                break;
        }

        return false;
    }

    @Override
    @Transactional
    public boolean hasOfficeRight(final User currentUser, @Nullable Integer officeId, final OfficeRight officeRight) {
        if (!currentUser.getCompany().isPresent() || !currentUser.getCompany().get().isEnabled()) {
            return false;
        }

        Company userCompany = currentUser.getCompany().get();

        switch (officeRight) {
            case CREATE:
                return hasAdminRights(currentUser);
            case VIEW:
                if (officeId == null) {
                    return true;
                } else {
                    return officeService.find(officeId)
                            .map(office -> office.getCompany().getId().equals(userCompany.getId()))
                            .orElse(false);
                }
            case DELETE:
            case EDIT:
                Optional<Office> subject = officeService.find(officeId);
                if (!subject.isPresent()) {
                    return false;
                }
                if (!subject.get().getCompany().getId().equals(userCompany.getId())) {
                    return false;
                }
                return hasAdminRights(currentUser);
        }

        return false;
    }

    @Override
    @Transactional
    public boolean hasRoomRight(final User currentUser, @Nullable Integer roomId, final RoomRight roomRight) {
        if (!currentUser.getCompany().isPresent() || !currentUser.getCompany().get().isEnabled()) {
            return false;
        }

        Company userCompany = currentUser.getCompany().get();

        switch (roomRight) {
            case CREATE:
                return hasAdminRights(currentUser);
            case VIEW:
                return roomService.find(roomId)
                        .map(room -> room.getOffice().getCompany().getId().equals(userCompany.getId()))
                        .orElse(false);
            case DELETE:
            case EDIT:
                Optional<Room> subject = roomService.find(roomId);
                if (!subject.isPresent()) {
                    return false;
                }
                if (!subject.get().getOffice().getCompany().getId().equals(userCompany.getId())) {
                    return false;
                }
                return hasAdminRights(currentUser);
        }

        return false;
    }

    @Override
    @Transactional
    public boolean hasDeskRight(final User currentUser, @Nullable Integer deskId, final DeskRight deskRight) {
        if (!currentUser.getCompany().isPresent() || !currentUser.getCompany().get().isEnabled()) {
            return false;
        }

        Company userCompany = currentUser.getCompany().get();

        switch (deskRight) {
            case CREATE:
                return hasAdminRights(currentUser);
            case VIEW:
                return deskService.find(deskId)
                        .map(room -> room.getRoom().getOffice().getCompany().getId().equals(userCompany.getId()))
                        .orElse(false);
            case DELETE:
            case EDIT:
                Optional<Desk> subject = deskService.find(deskId);
                if (!subject.isPresent()) {
                    return false;
                }
                if (!subject.get().getRoom().getOffice().getCompany().getId().equals(userCompany.getId())) {
                    return false;
                }
                return hasAdminRights(currentUser);
        }

        return false;
    }

    @Override
    public boolean isUserCanBeAddedToDepartment(final Integer userId, final Integer departmentId) {
        final User user = getUser(userId);
        final Department department = getDepartment(departmentId);

        if (!user.getCompany().isPresent() || !user.getCompany().get().isEnabled() || !department.getCompany().isEnabled()) {
            return false;
        }

        if (!user.getCompany().get().getId().equals(department.getCompany().getId())) {
            return false;
        }

        if (department.getOwner().isPresent() && !department.getOwner().get().getId().equals(userId)) {
            // avoid cases when participant is manager for its manager
            return !relationService.isManager(user, department.getOwner().get());
        }

        return true;
    }

    @Override
    public boolean isUserCanBeRemovedFromDepartment(final Integer userId, final Integer departmentId) {
        final Department department = getDepartment(departmentId);
        final User user = getUser(userId);

        if (!user.getCompany().isPresent() || !user.getCompany().get().isEnabled() || !department.getCompany().isEnabled()) {
            return false;
        }

        if (!user.getCompany().get().getId().equals(department.getCompany().getId())) {
            return false;
        }

        return !department.getOwner().isPresent() || !department.getOwner().get().getId().equals(userId);
    }

    private User getUser(@NotNull final Integer userId) {
        return userService.find(userId)
                .orElseThrow(() -> new ServiceException("Can't find user with id " + userId, ServiceException.Type.NOT_FOUND));
    }

    private Department getDepartment(@NotNull final Integer departmentId) {
        return departmentService.find(departmentId)
                .orElseThrow(() -> new ServiceException("Can't find department with id " + departmentId, ServiceException.Type.NOT_FOUND));
    }

    private static boolean hasAdminRights(final User user) {
        return user.getRole() == Role.Admin;
    }

    private static boolean hasManagerRights(final User user) {
        return user.getRole() == Role.Admin || user.getRole() == Role.Manager;
    }

}
