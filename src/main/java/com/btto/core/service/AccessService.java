package com.btto.core.service;

import com.btto.core.domain.User;
import javax.annotation.Nullable;

public interface AccessService {
    boolean isAdmin(User user);

    boolean hasCompanyRight(User currentUser, @Nullable Integer companyId, CompanyRight right);

    boolean hasUserRight(User currentUser, @Nullable Integer userId, UserRight right);

    boolean hasDepartmentRight(User currentUser, @Nullable Integer departmentId, DepartmentRight departmentRight);

    boolean hasWorkSessionRight(User currentUser, Integer ownerId, WorkSessionRight workSessionRight);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean hasOfficeRight(User currentUser, @Nullable Integer officeId, OfficeRight officeRight);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean hasRoomRight(User currentUser, @Nullable Integer roomId, RoomRight roomRight);

    boolean isUserCanBeAddedToDepartment(Integer userId, Integer departmentId);

    boolean isUserCanBeRemovedFromDepartment(Integer userId, Integer departmentId);

    enum CompanyRight {
        CREATE, REMOVE, VIEW, EDIT
    }

    enum UserRight {
        CREATE, REMOVE, VIEW, EDIT, SET_STATUS, GET_STATUS
    }

    enum DepartmentRight {
        CREATE, REMOVE, VIEW, VIEW_ALL, EDIT, ADD_PARTICIPANT, ASSIGN
    }

    enum WorkSessionRight {
        CREATE ,VIEW, EDIT, DELETE
    }

    enum OfficeRight {
        CREATE, VIEW, EDIT, DELETE
    }

    enum RoomRight {
        CREATE, VIEW, EDIT, DELETE
    }
}
