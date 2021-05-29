package com.btto.core.service;

import com.btto.core.domain.Company;
import com.btto.core.domain.Office;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface OfficeService extends AbstractEntityService<Office> {

    @Transactional
    Office create(@NotNull Company company, String name, String address);

    @Transactional
    Office update(Integer id, @Nullable String name, @Nullable String address);

    List<Office> getCompanyOffices(Company company);

    @Transactional
    void delete(Integer officeId);

    @Transactional
    List<String> getOfficeLevels(@NotNull Integer officeId);
}
