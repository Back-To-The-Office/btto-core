package com.btto.core.service;

import com.btto.core.dao.OfficeDao;
import com.btto.core.domain.Company;
import com.btto.core.domain.Office;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Service
public class OfficeServiceImpl extends AbstractEntityServiceImpl<Office, OfficeDao> implements OfficeService {

    @Autowired
    public OfficeServiceImpl(OfficeDao dao) {
        super(dao);
    }

    @Override
    @Transactional
    public Office create(@NotNull Company company, String name, String address) {
        final Office office = new Office();
        office.setCompany(company);
        office.setName(name);
        office.setAddress(address);
        return dao.merge(office);
    }

    @Override
    @Transactional
    public Office update(Integer id, @Nullable String name, @Nullable String address) {
        final Office office = getOffice(id);

        if (address != null) {
            office.setAddress(address);
        }

        if (name != null) {
            office.setName(name);
        }

        return dao.merge(office);
    }

    @Override
    public List<Office> getCompanyOffices(Company company) {
        return dao.findAllByCompany(company);
    }

    @Override
    @Transactional
    public void delete(final Integer officeId) {
        dao.deleteById(officeId);
    }

    private Office getOffice(final Integer id) {
        return Optional.ofNullable(dao.findOne(id))
                .orElseThrow(() -> new ServiceException("Can't find office with id: " + id, ServiceException.Type.NOT_FOUND));
    }

}
