package com.btto.core.service;

import com.btto.core.domain.Department;
import com.btto.core.domain.User;
import com.btto.core.mock.MockDepartmentBuilder;
import com.btto.core.mock.MockUserBuilder;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class RelationServiceImplTest {

    private final RelationService relationService = new RelationServiceImpl();

    @Test
    void testIsDirectManagerPositive() {
        final int managerId = 1;
        final int userId = 2;
        final User manager = new MockUserBuilder(managerId).build();
        final Department department = new MockDepartmentBuilder(1, null).owner(manager).build();
        final User user = new MockUserBuilder(userId).departments(department).build();

        assertTrue(relationService.isDirectManager(manager, user));
    }

    @Test
    void testIsDirectManagerNegative() {
        final int[] managersIds = new int[] {1, 2};
        final User[] managers = new User[] {
                new MockUserBuilder(managersIds[0]).build(),
                new MockUserBuilder(managersIds[1]).build()};
        final Department department = new MockDepartmentBuilder(1, null).owner(managers[0]).build();

        final int userId = 3;
        final User user = new MockUserBuilder(3).departments(department).build();

        assertFalse(relationService.isDirectManager(managers[1], user));
    }

    @Test
    void testIsDirectManagerWhenManagerDoesntSet() {
        final int managerId = 1;
        final User manager = new MockUserBuilder(managerId).build();
        final Department department = new MockDepartmentBuilder(1, null).build();
        final int userId = 3;
        final User user = new MockUserBuilder(userId).departments(department).build();

        assertFalse(relationService.isDirectManager(manager, user));
    }

    @Test
    void testIsManagerWhenManagerIsDirect() {
        final int managerId = 1;
        final int userId = 2;
        final User manager = new MockUserBuilder(managerId).build();
        final Department department = new MockDepartmentBuilder(1, null).owner(manager).build();
        final User user = new MockUserBuilder(userId).departments(department).build();

        assertTrue(relationService.isManager(manager, user));
    }

    @Test
    void testIsManagerWhenManagerIsAboveDirectManager() {
        final int[] managersIds = new int[] {1, 2};
        final User[] managers = new User[2];
        managers[0] = new MockUserBuilder(managersIds[0]).build();
        final Department managerDepartment = new MockDepartmentBuilder(1, null).owner(managers[0]).build();
        managers[1] = new MockUserBuilder(managersIds[1]).departments(managerDepartment).build();
        final Department userDepartment = new MockDepartmentBuilder(2, null).owner(managers[1]).build();
        final int userId = 3;
        final User user = new MockUserBuilder(userId).departments(userDepartment).build();

        assertTrue(relationService.isManager(managers[1], user));
    }

    @Test
    void testIsManagerWithLoop() {
        final int[] managersIds = new int[] {10, 11, 20, 21};
        final User[] managers = new User[4];
        managers[0] = new MockUserBuilder(managersIds[0]).build();
        managers[1] = new MockUserBuilder(managersIds[1]).build();

        final Department midLevelManager1Department = new MockDepartmentBuilder(1, null).owner(managers[0]).build();
        final Department midLevelManager2Department = new MockDepartmentBuilder(2, null).owner(managers[1]).build();

        managers[2] = new MockUserBuilder(managersIds[2]).departments(midLevelManager1Department).build();
        managers[3] = new MockUserBuilder(managersIds[3]).departments(midLevelManager2Department).build();

        final Department userDepartment1 = new MockDepartmentBuilder(3,null).owner(managers[2]).build();
        final Department userDepartment2 = new MockDepartmentBuilder(4,null).owner(managers[3]).build();

        final int userId = 30;
        final User user = new MockUserBuilder(userId).departments(userDepartment1, userDepartment2).build();

        // add loop
        final Department topManagerDepartment = new MockDepartmentBuilder(5, null).owner(user).build();
        when(managers[0].getDepartments()).thenReturn(ImmutableSet.of(topManagerDepartment));

        assertTrue(relationService.isManager(managers[0], user));
        assertTrue(relationService.isManager(managers[1], user));
        assertTrue(relationService.isManager(managers[2], user));
        assertTrue(relationService.isManager(managers[3], user));
    }

    @Test
    void testGetAllManagersWithCircle() {
        final int[] managersIds = new int[] {10, 11, 20, 21};
        final User[] managers = new User[4];
        managers[0] = new MockUserBuilder(managersIds[0]).build();
        managers[1] = new MockUserBuilder(managersIds[1]).build();

        final Department midLevelManager1Department = new MockDepartmentBuilder(1, null).owner(managers[0]).build();
        final Department midLevelManager2Department = new MockDepartmentBuilder(2, null).owner(managers[1]).build();

        managers[2] = new MockUserBuilder(managersIds[2]).departments(midLevelManager1Department).build();
        managers[3] = new MockUserBuilder(managersIds[3]).departments(midLevelManager2Department).build();

        final Department userDepartment1 = new MockDepartmentBuilder(3,null).owner(managers[2]).build();
        final Department userDepartment2 = new MockDepartmentBuilder(4,null).owner(managers[3]).build();

        final int userId = 30;
        final User user = new MockUserBuilder(userId).departments(userDepartment1, userDepartment2).build();

        // add loop
        final Department topManagerDepartment = new MockDepartmentBuilder(5, null).owner(user).build();
        when(managers[0].getDepartments()).thenReturn(ImmutableSet.of(topManagerDepartment));

        Set<User> result = relationService.getAllManagers(user);

        assertNotNull(result);
        assertTrue(result.contains(managers[0]));
        assertTrue(result.contains(managers[1]));
        assertTrue(result.contains(managers[2]));
        assertTrue(result.contains(managers[3]));
    }
}
