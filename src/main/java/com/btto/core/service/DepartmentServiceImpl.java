package com.btto.core.service;

import com.btto.core.dao.DepartmentDao;
import com.btto.core.domain.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DepartmentServiceImpl extends AbstractEntityServiceImpl<Department> implements DepartmentService {
    public DepartmentServiceImpl(@Autowired DepartmentDao departmentDao) {
        super(departmentDao);
    }
}
