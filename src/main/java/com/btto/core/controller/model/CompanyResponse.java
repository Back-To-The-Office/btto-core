package com.btto.core.controller.model;

import com.btto.core.domain.Company;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompanyResponse {
    private final int id;
    private final String name;

    public static CompanyResponse fromCompanyDomain(final Company company) {
        return new CompanyResponse(company.getId(), company.getName());
    }
}
