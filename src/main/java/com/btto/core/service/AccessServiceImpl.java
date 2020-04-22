package com.btto.core.service;

import com.btto.core.domain.Company;
import com.btto.core.domain.Department;
import com.btto.core.domain.User;
import com.btto.core.domain.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public final class AccessServiceImpl implements AccessService {

    private final UserService userService;
    private final DepartmentService departmentService;
    private final RelationService relationService;

    public AccessServiceImpl(@Autowired UserService userService,
                             @Autowired DepartmentService departmentService,
                             @Autowired RelationService relationService) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.relationService = relationService;
    }

    @Override
    public boolean isAdmin(final User user) {
        checkNotNull(user);
        return user.getRole().equals(Role.Admin);
    }

    @Override
    public boolean hasCompanyRight(final User currentUser, @Nullable final Integer companyId, final CompanyRight right) {
        switch (right) {
            case VIEW:
                checkNotNull(companyId);
                if (currentUser.getCompany().isPresent()) {
                    final Company userCompany = currentUser.getCompany().get();
                    if (hasAdminRights(currentUser) || userCompany.isEnabled()) {
                        return currentUser.getCompany().get().getId().equals(companyId);
                    }
                }
                break;
            case EDIT:
            case REMOVE:
                checkNotNull(companyId);
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
    public boolean hasUserRight(final User currentUser, @Nullable final Integer userId, final UserRight right) {
        switch (right) {
            case GET_STATUS:
            case VIEW: {
                checkNotNull(userId);
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
                checkNotNull(userId);
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
                checkNotNull(userId);
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
    public boolean hasDepartmentRight(final User currentUser, @Nullable final Integer departmentId, final DepartmentRight departmentRight) {
        if (!currentUser.getCompany().isPresent() || !currentUser.getCompany().get().isEnabled()) {
            return false;
        }
        final Integer currentUserCompanyId = currentUser.getCompany().get().getId();

        switch (departmentRight) {
            case VIEW: {
                checkNotNull(departmentId);
                return getDepartment(departmentId).getCompany().getId().equals(currentUserCompanyId);
            }
            case EDIT:
            case REMOVE: {
                checkNotNull(departmentId);
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
                checkNotNull(departmentId);
                final Department subject = getDepartment(departmentId);
                if (subject.getCompany().getId().equals(currentUserCompanyId)) {
                    if (hasAdminRights(currentUser)) {
                        return true;
                    } else if (hasManagerRights(currentUser)) {
                        return !subject.getOwner().isPresent() || subject.getOwner().get().getId().equals(currentUser.getId());
                    }
                }
            } break;
            case ADD_PARTICIPANT: {
                checkNotNull(departmentId);
                final Department subject = getDepartment(departmentId);
                if (subject.getCompany().getId().equals(currentUserCompanyId)) {
                    if (hasAdminRights(currentUser)) {
                        return true;
                    } else if (hasManagerRights(currentUser) && subject.getOwner().isPresent()) {
                        return subject.getOwner().get().getId().equals(currentUser.getId());
                    }
                }
            } break;
        }
        return false;
    }

    @Override
    public boolean hasWorkDayRight(final User currentUser, final Integer ownerId, final WorkDayRight workDayRight) {
        if (!currentUser.getCompany().isPresent() || !currentUser.getCompany().get().isEnabled()) {
            return false;
        }
        final User owner = getUser(ownerId);
        if (!owner.getCompany().isPresent() || !owner.getCompany().get().getId().equals(currentUser.getCompany().get().getId())) {
            return false;
        }

        switch (workDayRight) {
            case VIEW:
            case SUBTRACT_TIME:
                if (hasAdminRights(currentUser) || currentUser.getId().equals(ownerId) || relationService.isManager(currentUser, owner)) {
                    return true;
                }
                break;
            case ADD_TIME:
                if (hasAdminRights(currentUser) || relationService.isManager(currentUser, owner)) {
                    return true;
                }
                break;
        }

        return false;
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
