package com.btto.core.controller.model;

import com.btto.core.domain.Department;
import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DepartmentResponse {
    private final int id;
    private final String name;
    private final int companyId;
    private final int ownerId;
    private final Set<Integer> participantsIds;


    public static DepartmentResponse fromDepartmentDomain(final Department department) {
        return new DepartmentResponse(
                department.getId(),
                department.getName(),
                department.getCompany().getId(),
                department.getOwner().orElseThrow(() -> new IllegalStateException("Department " + department.getId() + " doesn't have any owner")).getId(),
                department.getParticipants().stream()
                    .map(participant -> participant.getParticipant().getId())
                    .collect(ImmutableSet.toImmutableSet()));
    }
}
