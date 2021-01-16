package com.btto.core.dao;

import com.btto.core.domain.Company;
import com.btto.core.domain.Department;

import java.util.List;

public interface DepartmentDao extends AbstractJpaDao<Department> {
    List<Department> getCompanyDepartments(Company companyId);
}
