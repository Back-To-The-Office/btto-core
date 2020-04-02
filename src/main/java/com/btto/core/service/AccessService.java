package com.btto.core.service;

import com.btto.core.domain.User;
import javax.annotation.Nullable;

public interface AccessService {

    boolean hasCompanyRight(User currentUser, @Nullable Integer companyId, CompanyRight right);

    boolean hasUserRight(User currentUser, @Nullable Integer userId, UserRight right);

    boolean hasDepartmentRight(User currentUser, @Nullable Integer departmentId, DepartmentRight departmentRight);

    boolean hasWorkDayRight(User currentUser, Integer ownerId, WorkDayRight workDayRight);

    enum CompanyRight {
        CREATE, REMOVE, VIEW, EDIT
    }

    enum UserRight {
        CREATE, REMOVE, VIEW, EDIT, SET_STATUS, GET_STATUS
    }

    enum DepartmentRight {
        CREATE, REMOVE, VIEW, EDIT, ADD_PARTICIPANT, ASSIGN
    }

    enum WorkDayRight {
        VIEW, ADD_TIME, SUBTRACT_TIME
    }
}
