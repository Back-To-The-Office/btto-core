package com.btto.core.dao;

import com.btto.core.domain.Company;
import com.btto.core.domain.Office;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OfficeDaoImpl extends AbstractJpaDaoImpl<Office> implements OfficeDao {

    public OfficeDaoImpl() {
        super(Office.class);
    }

    @Override
    public List<Office> findAllByCompany(Company company) {
        return entityManager.createQuery("from " + Office.class.getName() + " o where o.company = :company", Office.class)
                .setParameter("company", company)
                .getResultList();
    }
}
