package com.btto.core.mock;

import com.btto.core.domain.Company;
import com.btto.core.domain.Department;
import com.btto.core.domain.User;
import com.btto.core.domain.enums.Role;
import com.google.common.collect.ImmutableSet;

import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockUserBuilder {
    private final Integer id;
    private Role role = null;
    private Company company = null;
    private Set<Department> departments = null;

    public MockUserBuilder(final Integer id) {
        this.id = id;
    }

    public MockUserBuilder role(final Role role) {
        this.role = role;
        return this;
    }

    public MockUserBuilder company(final Company company) {
        this.company = company;
        return this;
    }

    public MockUserBuilder departments(final Set<Department> departments) {
        this.departments = departments;
        return this;
    }

    public MockUserBuilder departments(final Department... departments) {
        return departments(ImmutableSet.copyOf(departments));
    }

    public User build() {
        final User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        if (role != null) {
            when(user.getRole()).thenReturn(role);
        }
        if (company != null) {
            when(user.getCompany()).thenReturn(Optional.of(company));
        }
        if (departments != null) {
            when(user.getDepartments()).thenReturn(departments);
        } else {
            when(user.getDepartments()).thenReturn(ImmutableSet.of());
        }
        return user;
    }
}
