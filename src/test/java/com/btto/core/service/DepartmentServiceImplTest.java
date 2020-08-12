package com.btto.core.service;

import com.btto.core.dao.DepartmentDao;
import com.btto.core.dao.ParticipantDao;
import com.btto.core.domain.Company;
import com.btto.core.domain.Department;
import com.btto.core.domain.Participant;
import com.btto.core.domain.User;
import com.btto.core.mock.MockCompanyBuilder;
import com.btto.core.mock.MockDepartmentBuilder;
import com.btto.core.mock.MockUserBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DepartmentServiceImplTest {

    private final ParticipantDao participantDao = mock(ParticipantDao.class);
    private final DepartmentDao departmentDao = mock(DepartmentDao.class);
    private final UserService userService = mock(UserService.class);

    private final DepartmentServiceImpl departmentService = new DepartmentServiceImpl(departmentDao, participantDao, userService);

    @Test
    void testCreateDepartment() {
        final Company company = new MockCompanyBuilder(1).build();
        final User creator = new MockUserBuilder(1).company(company).build();

        final String departmentName = "departmentName";

        final ArgumentCaptor<Department> departmentArgumentCaptor = ArgumentCaptor.forClass(Department.class);
        final ArgumentCaptor<Participant> participantArgumentCaptor = ArgumentCaptor.forClass(Participant.class);

        departmentService.create(departmentName, creator);

        verify(departmentDao, times(1)).create(departmentArgumentCaptor.capture());
        verify(participantDao, times(1)).create(participantArgumentCaptor.capture());

        final Department department = departmentArgumentCaptor.getValue();
        assertEquals(department.getCompany(), company);
        assertTrue(department.getOwner().isPresent());
        assertEquals(department.getOwner().get(), creator);
        assertEquals(department.getName(), departmentName);

        final Participant participant = participantArgumentCaptor.getValue();
        assertEquals(participant.getDepartment(), department);
        assertEquals(participant.getParticipant(), creator);
    }

    @Test
    void testDelete() {
        departmentService.delete(1);
        verify(departmentDao, times(1)).deleteById(eq(1));
    }

    @Test
    void testUpdate() {
        final Department department = new Department();
        department.setName("name1");
        department.setId(1);

        when(departmentDao.findOne(eq(1))).thenReturn(department);

        departmentService.update(1, "name2");

        verify(departmentDao, times(1)).update(eq(department));

        assertEquals(department.getName(), "name2");
    }

    @Test
    void testUpdateWithNullableName() {
        final Department department = new Department();
        department.setName("name1");
        department.setId(1);

        when(departmentDao.findOne(eq(1))).thenReturn(department);

        departmentService.update(1, null);

        verify(departmentDao, times(1)).update(eq(department));

        assertEquals(department.getName(), "name1");
    }

    @Test
    void testAssign() {
        final Company company = new MockCompanyBuilder(1).build();
        final User owner1 = new MockUserBuilder(1).company(company).build();
        final User owner2 = new MockUserBuilder(2).company(company).build();

        final Department department = new Department();
        department.setName("name1");
        department.setOwner(owner1);

        when(departmentDao.findOne(eq(1))).thenReturn(department);
        when(userService.find(eq(2))).thenReturn(Optional.of(owner2));

        departmentService.assign(1, 2);

        verify(departmentDao, times(1)).update(eq(department));

        assertTrue(department.getOwner().isPresent());
        assertEquals(department.getOwner().get(), owner2);
    }

    @Test
    void testAddParticipant() {
        final Company company = new MockCompanyBuilder(1).build();
        final User creator = new MockUserBuilder(1).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).owner(creator).build();
        final User user = new MockUserBuilder(2).company(company).build();

        final ArgumentCaptor<Participant> participantArgumentCaptor = ArgumentCaptor.forClass(Participant.class);

        when(departmentDao.findOne(eq(1))).thenReturn(department);
        when(userService.find(eq(2))).thenReturn(Optional.of(user));

        departmentService.addParticipant(1, 2);

        verify(participantDao, times(1)).create(participantArgumentCaptor.capture());

        final Participant participant = participantArgumentCaptor.getValue();

        assertEquals(participant.getDepartment(), department);
        assertEquals(participant.getParticipant(), user);

    }
}
