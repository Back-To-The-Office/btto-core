package com.btto.core.service;

import com.btto.core.domain.Company;
import com.btto.core.domain.User;

import javax.annotation.Nullable;

public interface CompanyService extends AbstractEntityService<Company> {
    Integer create(String name, User creator);

    void delete(Integer companyId);

    Company update(Integer companyId, @Nullable String name);
}
