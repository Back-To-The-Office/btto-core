package com.btto.core.dao;

import com.btto.core.domain.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentDao extends AbstractJpaDao<Department> {

    public DepartmentDao() {
        super(Department.class);
    }

}
