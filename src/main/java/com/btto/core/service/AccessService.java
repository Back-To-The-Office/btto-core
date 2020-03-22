package com.btto.core.service;

public interface AccessService {

    //boolean hasCompanyRights(final )

    enum CompanyRight {
        CREATE, REMOVE, VIEW, EDIT
    }

    enum UserRight {
        CREATE, REMOVE, VIEW, EDIT
    }

    enum DepartmentRight {
        CREATE, REMOVE, VIEW, EDIT, ADD_PARTICIPANT, ASSIGN
    }

    enum WorkDayRight {
        CREATE, REMOVE, VIEW, ADD_TIME, SUBTRACT_TIME
    }
}
