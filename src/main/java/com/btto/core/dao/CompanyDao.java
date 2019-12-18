package com.btto.core.dao;

import com.btto.core.domain.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyDao extends AbstractJpaDao<Company> {

    public CompanyDao() {
        super(Company.class);
    }

}
