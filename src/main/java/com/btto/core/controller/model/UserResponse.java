package com.btto.core.controller.model;

import com.btto.core.domain.Company;
import com.btto.core.domain.Department;
import com.btto.core.domain.User;
import com.google.common.collect.ImmutableSet;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.time.ZoneId;
import java.util.Set;

@SuppressWarnings("FieldCanBeLocal")
@Getter
@AllArgsConstructor
public class UserResponse {
    private final int id;
    @Nullable
    private final Integer companyId;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String contacts;
    private final ZoneId timezone;
    private final String position;
    private final Set<Integer> departmentsIds;

    public static UserResponse fromUserDomain(final User user) {
        return new UserResponse(
                user.getId(),
                user.getCompany().map(Company::getId).orElse(null),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName().orElse(null),
                user.getContacts().orElse(null),
                user.getTimezone(),
                user.getPosition().orElse(null),
                user.getDepartments().stream()
                    .map(Department::getId)
                    .collect(ImmutableSet.toImmutableSet()));
    }
}
