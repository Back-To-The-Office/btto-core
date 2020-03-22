package com.btto.core.dao;

import com.btto.core.domain.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentDaoImpl extends AbstractJpaDaoImpl<Department> implements DepartmentDao {

    public DepartmentDaoImpl() {
        super(Department.class);
    }

}
