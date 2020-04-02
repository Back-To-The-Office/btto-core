package com.btto.core.dao;

import com.btto.core.domain.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyDaoImpl extends AbstractJpaDaoImpl<Company> implements CompanyDao {

    public CompanyDaoImpl() {
        super(Company.class);
    }

}
