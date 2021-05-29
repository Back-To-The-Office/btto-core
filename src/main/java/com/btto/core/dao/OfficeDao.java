package com.btto.core.dao;

import com.btto.core.domain.Company;
import com.btto.core.domain.Office;

import java.util.List;

public interface OfficeDao extends AbstractJpaDao<Office> {
    List<Office> findAllByCompany(Company company);
}
