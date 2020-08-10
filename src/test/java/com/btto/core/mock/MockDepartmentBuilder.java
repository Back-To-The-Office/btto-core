package com.btto.core.mock;

import com.btto.core.domain.Company;
import com.btto.core.domain.Department;
import com.btto.core.domain.Participant;
import com.btto.core.domain.User;
import com.google.common.collect.ImmutableSet;

import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockDepartmentBuilder {
    private final Integer id;
    private final Company company;

    private User owner = null;
    private Set<User> participants = null;

    public MockDepartmentBuilder(final Integer id, final Company company) {
        this.id = id;
        this.company = company;
    }

    public MockDepartmentBuilder owner(final User owner) {
        this.owner = owner;
        return this;
    }

    public MockDepartmentBuilder participants(final Set<User> participants) {
        this.participants = participants;
        return this;
    }

    public Department build() {
        final Department department = mock(Department.class);
        when(department.getId()).thenReturn(id);
        when(department.getCompany()).thenReturn(company);
        when(department.getOwner()).thenReturn(Optional.ofNullable(owner));

        if (participants != null) {
            when(department.getParticipants()).thenReturn(participants.stream()
                    .map(participant -> buildMockParticipant(participant, department))
                    .collect(ImmutableSet.toImmutableSet()));
        }
        return department;
    }

    private static Participant buildMockParticipant(final User user, final Department department) {
        final Participant participant = mock(Participant.class);
        when(participant.getParticipant()).thenReturn(user);
        when(participant.getDepartment()).thenReturn(department);
        return participant;
    }
}
