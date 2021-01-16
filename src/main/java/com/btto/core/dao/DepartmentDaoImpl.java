package com.btto.core.dao;

import com.btto.core.domain.Company;
import com.btto.core.domain.Department;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DepartmentDaoImpl extends AbstractJpaDaoImpl<Department> implements DepartmentDao {

    public DepartmentDaoImpl() {
        super(Department.class);
    }

    @Override
    public List<Department> getCompanyDepartments(final Company company) {
        return entityManager.createQuery(
            "from " + Department.class.getName() +
                " d where d.company = :company order by d.id",
            Department.class)
            .setParameter("company", company)
            .getResultList();
    }
}
