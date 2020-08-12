package com.btto.core.service;

import com.btto.core.dao.CompanyDao;
import com.btto.core.dao.UserDao;
import com.btto.core.domain.Company;
import com.btto.core.domain.User;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.Optional;

@Service
public class CompanyServiceImpl extends AbstractEntityServiceImpl<Company, CompanyDao> implements CompanyService {

    private final UserDao userDao;

    @Autowired
    public CompanyServiceImpl(final CompanyDao dao, UserDao userDao) {
        super(dao);
        this.userDao = userDao;
    }

    @Override
    @Transactional
    public void create(final String name, final User creator) {
        final Company company = new Company();
        company.setName(name);
        company.setUsers(ImmutableSet.of(creator));
        dao.create(company);

        creator.setCompany(company);
        userDao.update(creator);
    }

    @Override
    @Transactional
    public void delete(final Integer companyId) {
        final Company company = Optional.ofNullable(dao.findOne(companyId))
                .orElseThrow(() -> new ServiceException("Can't find company with id: " + companyId, ServiceException.Type.NOT_FOUND));

        company.setEnabled(false);

        dao.update(company);
    }

    @Override
    @Transactional
    public Company update(final Integer companyId, @Nullable final String name) {
        final Company company = Optional.ofNullable(dao.findOne(companyId))
                .orElseThrow(() -> new ServiceException("Can't find company with id: " + companyId, ServiceException.Type.NOT_FOUND));

        if (StringUtils.isNotBlank(name)) {
            company.setName(name);
        }

        return dao.update(company);
    }
}
