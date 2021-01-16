package com.btto.core.service;

import com.btto.core.domain.Company;
import com.btto.core.domain.Department;
import com.btto.core.domain.User;
import com.btto.core.domain.enums.Role;
import com.btto.core.mock.MockCompanyBuilder;
import com.btto.core.mock.MockDepartmentBuilder;
import com.btto.core.mock.MockUserBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccessServiceImplTest {

    private final UserService userService = mock(UserService.class);
    private final DepartmentService departmentService = mock(DepartmentService.class);
    private final RelationService relationService = mock(RelationService.class);

    private final AccessService accessService = new AccessServiceImpl(userService, departmentService, relationService);

    @Test
    void testThatUserCanViewCompany() {
        final Company company = new MockCompanyBuilder(1).build();
        final User user = new MockUserBuilder(1).company(company).build();

        assertTrue(accessService.hasCompanyRight(user, 1, AccessService.CompanyRight.VIEW));
    }

    @Test
    void testThatUserCantViewDisabledCompany() {
        final Company company = new MockCompanyBuilder(1).disabled().build();
        final User user = new MockUserBuilder(1).company(company).build();

        assertFalse(accessService.hasCompanyRight(user, 1, AccessService.CompanyRight.VIEW));
    }

    @Test
    void testThatAdminCantViewDisabledCompany() {
        final Company company = new MockCompanyBuilder(1).disabled().build();
        final User user = new MockUserBuilder(1).role(Role.Admin).company(company).build();

        assertTrue(accessService.hasCompanyRight(user, 1, AccessService.CompanyRight.VIEW));
    }

    @Test
    void testThatUserWithoutCompanyCantViewCompany() {
        final User user = new MockUserBuilder(1).build();

        assertFalse(accessService.hasCompanyRight(user, 1, AccessService.CompanyRight.VIEW));
    }

    @Test
    void testThatUserWithoutOneCompanyCantViewAnotherCompany() {
        final Company someCompany = new MockCompanyBuilder(1).build();
        final Company userCompany = new MockCompanyBuilder(2).build();
        final User user = new MockUserBuilder(1).company(userCompany).build();

        assertFalse(accessService.hasCompanyRight(user, someCompany.getId(), AccessService.CompanyRight.VIEW));
    }

    @Test
    void testThatUserCantEditOwnCompany() {
        final Company company = new MockCompanyBuilder(1).build();
        final User user = new MockUserBuilder(1).company(company).build();

        assertFalse(accessService.hasCompanyRight(user, 1, AccessService.CompanyRight.EDIT));
    }

    @Test
    void testThatUserCantRemoveOwnCompany() {
        final Company company = new MockCompanyBuilder(1).build();
        final User user = new MockUserBuilder(1).company(company).build();

        assertFalse(accessService.hasCompanyRight(user, 1, AccessService.CompanyRight.REMOVE));
    }

    @Test
    void testThatAdminCanEditOwnCompany() {
        final Company company = new MockCompanyBuilder(1).build();
        final User admin = new MockUserBuilder(1).company(company).role(Role.Admin).build();

        assertTrue(accessService.hasCompanyRight(admin, 1, AccessService.CompanyRight.EDIT));
    }

    @Test
    void testThatAdminCanRemoveOwnCompany() {
        final Company company = new MockCompanyBuilder(1).build();
        final User admin = new MockUserBuilder(1).company(company).role(Role.Admin).build();

        assertTrue(accessService.hasCompanyRight(admin, 1, AccessService.CompanyRight.REMOVE));
    }

    @Test
    void testThatAdminCantEditAnotherCompany() {
        final Company someCompany = new MockCompanyBuilder(1).build();
        final Company adminCompany = new MockCompanyBuilder(2).build();
        final User admin = new MockUserBuilder(1).role(Role.Admin).company(adminCompany).build();

        assertFalse(accessService.hasCompanyRight(admin, someCompany.getId(), AccessService.CompanyRight.EDIT));
    }

    @Test
    void testThatAdminCantRemoveAnotherCompany() {
        final Company someCompany = new MockCompanyBuilder(1).build();
        final Company adminCompany = new MockCompanyBuilder(2).build();
        final User user = new MockUserBuilder(1).role(Role.Admin).company(adminCompany).build();

        assertFalse(accessService.hasCompanyRight(user, someCompany.getId(), AccessService.CompanyRight.REMOVE));
    }

    @Test
    void testThatAdminCanCreateCompany() {
        final User admin = new MockUserBuilder(1).role(Role.Admin).build();

        assertTrue(accessService.hasCompanyRight(admin, null, AccessService.CompanyRight.CREATE));
    }

    @Test
    void testThatAdminCantCreateCompanyIfItHasOne() {
        final Company company = new MockCompanyBuilder(1).build();
        final User admin = new MockUserBuilder(1).company(company).role(Role.Admin).build();

        assertFalse(accessService.hasCompanyRight(admin, null, AccessService.CompanyRight.CREATE));
    }

    @Test
    void testThatAdminCanCreateCompanyIfItHasDisabledCompany() {
        final Company company = new MockCompanyBuilder(1).disabled().build();
        final User admin = new MockUserBuilder(1).company(company).role(Role.Admin).build();

        assertTrue(accessService.hasCompanyRight(admin, null, AccessService.CompanyRight.CREATE));
    }

    @Test
    void testThatUserCantCreateCompany() {
        final User user = new MockUserBuilder(1).build();

        assertFalse(accessService.hasCompanyRight(user, null, AccessService.CompanyRight.CREATE));
    }

    @Test
    void testThatUserWithoutCompanyCanGetOwnStatus() {
        final User user = new MockUserBuilder(1).build();
        assertTrue(accessService.hasUserRight(user, user.getId(), AccessService.UserRight.GET_STATUS));
    }

    @Test
    void testThatAdminWithoutCompanyCantGetStatusOfUser() {
        final User admin = new MockUserBuilder(1).role(Role.Admin).build();
        final User user = new MockUserBuilder(2).role(Role.Admin).build();
        assertFalse(accessService.hasUserRight(admin, user.getId(), AccessService.UserRight.GET_STATUS));
    }


    @Test
    void testThatAdminCanGetStatusOfUserWithTheSameCompany() {
        final Company company = new MockCompanyBuilder(1).build();
        final User admin = new MockUserBuilder(1).role(Role.Admin).company(company).build();
        final User user = new MockUserBuilder(2).company(company).build();

        when(userService.find(user.getId())).thenReturn(Optional.of(user));

        assertTrue(accessService.hasUserRight(admin, user.getId(), AccessService.UserRight.GET_STATUS));
    }

    @Test
    void testThatAdminCantGetStatusOfUserWithTheSameDisabledCompany() {
        final Company company = new MockCompanyBuilder(1).disabled().build();
        final User admin = new MockUserBuilder(1).role(Role.Admin).company(company).build();
        final User user = new MockUserBuilder(2).company(company).build();

        when(userService.find(user.getId())).thenReturn(Optional.of(user));

        assertFalse(accessService.hasUserRight(admin, user.getId(), AccessService.UserRight.GET_STATUS));
    }

    @Test
    void testThatAdminCantGetStatusOfUserWithDifferentCompany() {
        final Company userCompany = new MockCompanyBuilder(1).build();
        final Company adminCompany = new MockCompanyBuilder(2).build();
        final User admin = new MockUserBuilder(1).role(Role.Admin).company(adminCompany).build();
        final User user = new MockUserBuilder(2).company(userCompany).build();

        when(userService.find(user.getId())).thenReturn(Optional.of(user));

        assertFalse(accessService.hasUserRight(admin, user.getId(), AccessService.UserRight.GET_STATUS));
    }

    @Test
    void testThatUserWithoutCompanyCanViewItself() {
        final User user = new MockUserBuilder(1).build();
        assertTrue(accessService.hasUserRight(user, user.getId(), AccessService.UserRight.VIEW));
    }

    @Test
    void testThatAdminWithoutCompanyCantViewUser() {
        final User admin = new MockUserBuilder(1).role(Role.Admin).build();
        final User user = new MockUserBuilder(2).role(Role.Admin).build();
        assertFalse(accessService.hasUserRight(admin, user.getId(), AccessService.UserRight.VIEW));
    }

    @Test
    void testThatAdminCanViewUserWithTheSameCompany() {
        final Company company = new MockCompanyBuilder(1).build();
        final User admin = new MockUserBuilder(1).role(Role.Admin).company(company).build();
        final User user = new MockUserBuilder(2).company(company).build();

        when(userService.find(user.getId())).thenReturn(Optional.of(user));

        assertTrue(accessService.hasUserRight(admin, user.getId(), AccessService.UserRight.VIEW));
    }

    @Test
    void testThatAdminCantViewUserWithTheSameDisabledCompany() {
        final Company company = new MockCompanyBuilder(1).disabled().build();
        final User admin = new MockUserBuilder(1).role(Role.Admin).company(company).build();
        final User user = new MockUserBuilder(2).company(company).build();

        when(userService.find(user.getId())).thenReturn(Optional.of(user));

        assertFalse(accessService.hasUserRight(admin, user.getId(), AccessService.UserRight.VIEW));
    }

    @Test
    void testThatAdminCantViewUserWithDifferentCompany() {
        final Company userCompany = new MockCompanyBuilder(1).build();
        final Company adminCompany = new MockCompanyBuilder(2).build();
        final User admin = new MockUserBuilder(1).role(Role.Admin).company(adminCompany).build();
        final User user = new MockUserBuilder(2).company(userCompany).build();

        when(userService.find(user.getId())).thenReturn(Optional.of(user));

        assertFalse(accessService.hasUserRight(admin, user.getId(), AccessService.UserRight.VIEW));
    }

    @Test
    void testThatUserWithoutCompanyCanEditItself() {
        final User user = new MockUserBuilder(1).build();

        assertTrue(accessService.hasUserRight(user, user.getId(), AccessService.UserRight.EDIT));
    }

    @Test
    void testThatAdminWithoutCompanyCantEdit() {
        final User admin = new MockUserBuilder(1).role(Role.Admin).build();

        assertFalse(accessService.hasUserRight(admin, 2, AccessService.UserRight.EDIT));
    }

    @Test
    void testThatAdminCanEditUserWithTheSameCompany() {
        final Company company = new MockCompanyBuilder(1).build();
        final User admin = new MockUserBuilder(1).role(Role.Admin).company(company).build();
        final User user = new MockUserBuilder(2).company(company).build();

        when(userService.find(user.getId())).thenReturn(Optional.of(user));

        assertTrue(accessService.hasUserRight(admin, user.getId(), AccessService.UserRight.EDIT));
    }

    @Test
    void testThatAdminCantEditUserWithTheSameDisabledCompany() {
        final Company company = new MockCompanyBuilder(1).disabled().build();
        final User admin = new MockUserBuilder(1).role(Role.Admin).company(company).build();
        final User user = new MockUserBuilder(2).company(company).build();

        when(userService.find(user.getId())).thenReturn(Optional.of(user));

        assertFalse(accessService.hasUserRight(admin, user.getId(), AccessService.UserRight.EDIT));
    }

    @Test
    void testThatAdminCantEditUserWithTheDifferentCompany() {
        final Company userCompany = new MockCompanyBuilder(1).build();
        final Company adminCompany = new MockCompanyBuilder(2).build();
        final User admin = new MockUserBuilder(1).role(Role.Admin).company(adminCompany).build();
        final User user = new MockUserBuilder(2).company(userCompany).build();

        when(userService.find(user.getId())).thenReturn(Optional.of(user));

        assertFalse(accessService.hasUserRight(admin, user.getId(), AccessService.UserRight.EDIT));
    }

    @Test
    void testThatUserWithoutCompanyCantRemoveItself() {
        final User user = new MockUserBuilder(1).build();

        assertFalse(accessService.hasUserRight(user, user.getId(), AccessService.UserRight.REMOVE));
    }

    @Test
    void testThatAdminWithoutCompanyCanRemoveItself() {
        final User admin = new MockUserBuilder(1).role(Role.Admin).build();

        assertTrue(accessService.hasUserRight(admin, admin.getId(), AccessService.UserRight.REMOVE));
    }

    @Test
    void testThatAdminWithoutCompanyCantRemoveUser() {
        final User admin = new MockUserBuilder(1).role(Role.Admin).build();

        assertFalse(accessService.hasUserRight(admin, 2, AccessService.UserRight.REMOVE));
    }

    @Test
    void testThatAdminCanRemoveUserWithTheSameCompany() {
        final Company company = new MockCompanyBuilder(1).build();
        final User admin = new MockUserBuilder(1).role(Role.Admin).company(company).build();
        final User user = new MockUserBuilder(2).company(company).build();

        when(userService.find(user.getId())).thenReturn(Optional.of(user));

        assertTrue(accessService.hasUserRight(admin, user.getId(), AccessService.UserRight.REMOVE));
    }

    @Test
    void testThatAdminCantRemoveUserWithTheSameDisabledCompany() {
        final Company company = new MockCompanyBuilder(1).disabled().build();
        final User admin = new MockUserBuilder(1).role(Role.Admin).company(company).build();
        final User user = new MockUserBuilder(2).company(company).build();

        when(userService.find(user.getId())).thenReturn(Optional.of(user));

        assertFalse(accessService.hasUserRight(admin, user.getId(), AccessService.UserRight.REMOVE));
    }

    @Test
    void testThatAdminCantRemoveUserWithTheDifferentCompany() {
        final Company userCompany = new MockCompanyBuilder(1).build();
        final Company adminCompany = new MockCompanyBuilder(2).build();
        final User admin = new MockUserBuilder(1).role(Role.Admin).company(adminCompany).build();
        final User user = new MockUserBuilder(2).company(userCompany).build();

        when(userService.find(user.getId())).thenReturn(Optional.of(user));

        assertFalse(accessService.hasUserRight(admin, user.getId(), AccessService.UserRight.EDIT));
    }

    @Test
    void testThatUserCantCreateUser() {
        final Company company = new MockCompanyBuilder(1).build();
        final User user = new MockUserBuilder(1).company(company).build();

        assertFalse(accessService.hasUserRight(user, null, AccessService.UserRight.CREATE));
    }

    @Test
    void testThatAdminCanCreateUser() {
        final Company company = new MockCompanyBuilder(1).build();
        final User admin = new MockUserBuilder(1).role(Role.Admin).company(company).build();

        assertTrue(accessService.hasUserRight(admin, null, AccessService.UserRight.CREATE));
    }

    @Test
    void testThatAdminWithDisabledCompanyCantCreateUser() {
        final Company company = new MockCompanyBuilder(1).disabled().build();
        final User admin = new MockUserBuilder(1).role(Role.Admin).company(company).build();

        assertFalse(accessService.hasUserRight(admin, null, AccessService.UserRight.CREATE));
    }

    @Test
    void testThatAdminWithoutCompanyCantCreateUser() {
        final User admin = new MockUserBuilder(1).role(Role.Admin).build();

        assertFalse(accessService.hasUserRight(admin, null, AccessService.UserRight.CREATE));
    }

    @Test
    void testThatUserCanSetStatus() {
        final Company company = new MockCompanyBuilder(1).build();
        final User user = new MockUserBuilder(1).company(company).build();

        assertTrue(accessService.hasUserRight(user, user.getId(), AccessService.UserRight.SET_STATUS));
    }

    @Test
    void testThatAdminCantSetStatusToAnotherUser() {
        final Company company = new MockCompanyBuilder(1).build();
        final User admin = new MockUserBuilder(1).company(company).role(Role.Admin).build();
        final User user = new MockUserBuilder(2).company(company).build();

        assertFalse(accessService.hasUserRight(admin, user.getId(), AccessService.UserRight.SET_STATUS));
    }

    @ParameterizedTest
    @EnumSource(AccessService.DepartmentRight.class)
    void testThatAdminWithoutCompanyCantDoAnythingWithDepartments(final AccessService.DepartmentRight right) {
        final User admin = new MockUserBuilder(1).role(Role.Admin).build();
        assertFalse(accessService.hasDepartmentRight(admin, null, right));
    }

    @ParameterizedTest
    @EnumSource(AccessService.DepartmentRight.class)
    void testThatAdminWithDisabledCompanyCantDoAnythingWithDepartments(final AccessService.DepartmentRight right) {
        final Company company = new MockCompanyBuilder(1).disabled().build();
        final User admin = new MockUserBuilder(1).role(Role.Admin).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).owner(admin).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertFalse(accessService.hasDepartmentRight(admin, department.getId(), right));
    }

    @Test
    void testThatUserCanViewDepartmentWithSameCompany() {
        final Company company = new MockCompanyBuilder(1).build();
        final User user = new MockUserBuilder(1).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertTrue(accessService.hasDepartmentRight(user, department.getId(), AccessService.DepartmentRight.VIEW));
    }

    @Test
    void testThatUserCantViewDepartmentWithAnotherCompany() {
        final Company userCompany = new MockCompanyBuilder(1).build();
        final Company departmentCompany = new MockCompanyBuilder(2).build();
        final User user = new MockUserBuilder(1).company(userCompany).build();
        final Department department = new MockDepartmentBuilder(1, departmentCompany).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertFalse(accessService.hasDepartmentRight(user, department.getId(), AccessService.DepartmentRight.VIEW));
    }

    @Test
    void testThatUserCantViewDepartmentsOfOwnCompany() {
        final Company userCompany = new MockCompanyBuilder(1).build();
        final Company departmentCompany = new MockCompanyBuilder(2).build();
        final User user = new MockUserBuilder(1).company(userCompany).build();
        final Department department = new MockDepartmentBuilder(1, departmentCompany).build();

        assertFalse(accessService.hasDepartmentRight(user, department.getId(), AccessService.DepartmentRight.VIEW_ALL));
    }

    @Test
    void testThatAdminCantEditDepartmentWithAnotherCompany() {
        final Company adminCompany = new MockCompanyBuilder(1).build();
        final Company departmentCompany = new MockCompanyBuilder(2).build();
        final User admin = new MockUserBuilder(1).role(Role.Admin).company(adminCompany).build();
        final Department department = new MockDepartmentBuilder(1, departmentCompany).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertFalse(accessService.hasDepartmentRight(admin, department.getId(), AccessService.DepartmentRight.EDIT));
    }

    @Test
    void testThatAdminCanEditDepartmentWithSameCompany() {
        final Company company = new MockCompanyBuilder(1).build();
        final User admin = new MockUserBuilder(1).role(Role.Admin).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertTrue(accessService.hasDepartmentRight(admin, department.getId(), AccessService.DepartmentRight.EDIT));
    }

    @Test
    void testThatManagerCanEditDepartmentIfItIsOwner() {
        final Company company = new MockCompanyBuilder(1).build();
        final User manager = new MockUserBuilder(1).role(Role.Manager).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).owner(manager).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertTrue(accessService.hasDepartmentRight(manager, department.getId(), AccessService.DepartmentRight.EDIT));
    }

    @Test
    void testThatManagerCantEditDepartmentIfItIsNotOwner() {
        final Company company = new MockCompanyBuilder(1).build();
        final User manager = new MockUserBuilder(1).role(Role.Manager).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertFalse(accessService.hasDepartmentRight(manager, department.getId(), AccessService.DepartmentRight.EDIT));
    }

    @Test
    void testThatUserCantEditDepartment() {
        final Company company = new MockCompanyBuilder(1).build();
        final User user = new MockUserBuilder(1).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertFalse(accessService.hasDepartmentRight(user, department.getId(), AccessService.DepartmentRight.EDIT));
    }

    @Test
    void testThatAdminCantRemoveDepartmentWithAnotherCompany() {
        final Company adminCompany = new MockCompanyBuilder(1).build();
        final Company departmentCompany = new MockCompanyBuilder(2).build();
        final User admin = new MockUserBuilder(1).role(Role.Admin).company(adminCompany).build();
        final Department department = new MockDepartmentBuilder(1, departmentCompany).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertFalse(accessService.hasDepartmentRight(admin, department.getId(), AccessService.DepartmentRight.REMOVE));
    }

    @Test
    void testThatAdminCanRemoveDepartmentWithSameCompany() {
        final Company company = new MockCompanyBuilder(1).build();
        final User admin = new MockUserBuilder(1).role(Role.Admin).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertTrue(accessService.hasDepartmentRight(admin, department.getId(), AccessService.DepartmentRight.REMOVE));
    }

    @Test
    void testThatAdminCantRemoveDepartmentWithSameDisabledCompany() {
        final Company company = new MockCompanyBuilder(1).disabled().build();
        final User admin = new MockUserBuilder(1).role(Role.Admin).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertFalse(accessService.hasDepartmentRight(admin, department.getId(), AccessService.DepartmentRight.REMOVE));
    }

    @Test
    void testThatManagerCanRemoveDepartmentIfItIsOwner() {
        final Company company = new MockCompanyBuilder(1).build();
        final User manager = new MockUserBuilder(1).role(Role.Manager).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).owner(manager).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertTrue(accessService.hasDepartmentRight(manager, department.getId(), AccessService.DepartmentRight.REMOVE));
    }

    @Test
    void testThatManagerCantRemoveDepartmentIfItIsNotOwner() {
        final Company company = new MockCompanyBuilder(1).build();
        final User manager = new MockUserBuilder(1).role(Role.Manager).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertFalse(accessService.hasDepartmentRight(manager, department.getId(), AccessService.DepartmentRight.REMOVE));
    }

    @Test
    void testThatUserCantRemoveDepartment() {
        final Company company = new MockCompanyBuilder(1).build();
        final User user = new MockUserBuilder(1).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertFalse(accessService.hasDepartmentRight(user, department.getId(), AccessService.DepartmentRight.REMOVE));
    }

    @Test
    void testThatAdminCanCreateDepartment() {
        final Company company = new MockCompanyBuilder(1).build();
        final User admin = new MockUserBuilder(1).company(company).role(Role.Admin).build();
        assertTrue(accessService.hasDepartmentRight(admin, null, AccessService.DepartmentRight.CREATE));
    }

    @Test
    void testThatAdminCantCreateDepartmentIfCompanyDisabled() {
        final Company company = new MockCompanyBuilder(1).disabled().build();
        final User admin = new MockUserBuilder(1).company(company).role(Role.Admin).build();
        assertFalse(accessService.hasDepartmentRight(admin, null, AccessService.DepartmentRight.CREATE));
    }

    @Test
    void testThatManagerCanCreateDepartment() {
        final Company company = new MockCompanyBuilder(1).build();
        final User manager = new MockUserBuilder(1).company(company).role(Role.Manager).build();
        assertTrue(accessService.hasDepartmentRight(manager, null, AccessService.DepartmentRight.CREATE));
    }

    @Test
    void testThatUserCantCreateDepartment() {
        final Company company = new MockCompanyBuilder(1).build();
        final User user = new MockUserBuilder(1).company(company).build();
        assertFalse(accessService.hasDepartmentRight(user, null, AccessService.DepartmentRight.CREATE));
    }

    @Test
    void testThatAdminWithAnotherCompanyCantAssignDepartment() {
        final Company adminCompany = new MockCompanyBuilder(1).build();
        final Company departmentCompany = new MockCompanyBuilder(2).build();
        final User admin = new MockUserBuilder(1).company(adminCompany).role(Role.Admin).build();
        final Department department = new MockDepartmentBuilder(1, departmentCompany).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertFalse(accessService.hasDepartmentRight(admin, department.getId(), AccessService.DepartmentRight.ASSIGN));
    }

    @Test
    void testThatAdminCanAssignDepartment() {
        final Company company = new MockCompanyBuilder(1).build();
        final User admin = new MockUserBuilder(1).company(company).role(Role.Admin).build();
        final Department department = new MockDepartmentBuilder(1, company).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertTrue(accessService.hasDepartmentRight(admin, department.getId(), AccessService.DepartmentRight.ASSIGN));
    }

    @Test
    void testThatAdminCanAssignDepartmentIfCompanyDisabled() {
        final Company company = new MockCompanyBuilder(1).disabled().build();
        final User admin = new MockUserBuilder(1).company(company).role(Role.Admin).build();
        final Department department = new MockDepartmentBuilder(1, company).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertFalse(accessService.hasDepartmentRight(admin, department.getId(), AccessService.DepartmentRight.ASSIGN));
    }

    @Test
    void testThatManagerCanAssignDepartmentIfItIsOwner() {
        final Company company = new MockCompanyBuilder(1).build();
        final User manager = new MockUserBuilder(1).company(company).role(Role.Manager).build();
        final Department department = new MockDepartmentBuilder(1, company).owner(manager).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertTrue(accessService.hasDepartmentRight(manager, department.getId(), AccessService.DepartmentRight.ASSIGN));
    }

    @Test
    void testThatManagerCanAssignDepartmentIfItIsOwnerOfDepartmentOwnerTeam() {
        final Company company = new MockCompanyBuilder(1).build();
        final User mainManager = new MockUserBuilder(1).company(company).role(Role.Manager).build();
        final Department managerDepartment = new MockDepartmentBuilder(1, company).owner(mainManager).build();

        final User manager = new MockUserBuilder(2).company(company).departments(managerDepartment).role(Role.Manager).build();

        final Department department = new MockDepartmentBuilder(2, company).owner(manager).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));
        when(relationService.isManager(eq(mainManager), eq(manager))).thenReturn(true);

        assertTrue(accessService.hasDepartmentRight(manager, department.getId(), AccessService.DepartmentRight.ASSIGN));
    }

    @Test
    void testThatManagerCanAssignDepartmentWithoutOwner() {
        final Company company = new MockCompanyBuilder(1).build();
        final User manager = new MockUserBuilder(1).company(company).role(Role.Manager).build();
        final Department department = new MockDepartmentBuilder(1, company).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertTrue(accessService.hasDepartmentRight(manager, department.getId(), AccessService.DepartmentRight.ASSIGN));
    }

    @Test
    void testThatManagerCantAssignDepartmentIfItIsNotOwner() {
        final Company company = new MockCompanyBuilder(1).build();
        final User manager = new MockUserBuilder(1).company(company).role(Role.Manager).build();
        final User departmentManager = new MockUserBuilder(2).company(company).role(Role.Manager).build();
        final Department department = new MockDepartmentBuilder(1, company).owner(departmentManager).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertFalse(accessService.hasDepartmentRight(manager, department.getId(), AccessService.DepartmentRight.ASSIGN));
    }

    @Test
    void testThatUserCantAssignDepartment() {
        final Company company = new MockCompanyBuilder(1).build();
        final User user = new MockUserBuilder(1).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertFalse(accessService.hasDepartmentRight(user, department.getId(), AccessService.DepartmentRight.ASSIGN));
    }

    @Test
    void testThatAdminWithAnotherCompanyCantAddParticipant() {
        final Company adminCompany = new MockCompanyBuilder(1).build();
        final Company departmentCompany = new MockCompanyBuilder(2).build();
        final User admin = new MockUserBuilder(1).company(adminCompany).role(Role.Admin).build();
        final Department department = new MockDepartmentBuilder(1, departmentCompany).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertFalse(accessService.hasDepartmentRight(admin, department.getId(), AccessService.DepartmentRight.ADD_PARTICIPANT));
    }

    @Test
    void testThatAdminCanAddParticipantToDepartment() {
        final Company company = new MockCompanyBuilder(1).build();
        final User admin = new MockUserBuilder(1).company(company).role(Role.Admin).build();
        final Department department = new MockDepartmentBuilder(1, company).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertTrue(accessService.hasDepartmentRight(admin, department.getId(), AccessService.DepartmentRight.ADD_PARTICIPANT));
    }

    @Test
    void testThatAdminCantAddParticipantToDepartmentIfCompanyDisabled() {
        final Company company = new MockCompanyBuilder(1).disabled().build();
        final User admin = new MockUserBuilder(1).company(company).role(Role.Admin).build();
        final Department department = new MockDepartmentBuilder(1, company).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertFalse(accessService.hasDepartmentRight(admin, department.getId(), AccessService.DepartmentRight.ADD_PARTICIPANT));
    }

    @Test
    void testThatManagerCanAddParticipantToDepartmentIfItIsOwner() {
        final Company company = new MockCompanyBuilder(1).build();
        final User manager = new MockUserBuilder(1).company(company).role(Role.Manager).build();
        final Department department = new MockDepartmentBuilder(1, company).owner(manager).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertTrue(accessService.hasDepartmentRight(manager, department.getId(), AccessService.DepartmentRight.ADD_PARTICIPANT));
    }

    @Test
    void testThatManagerCantAddParticipantToDepartmentWithoutOwner() {
        final Company company = new MockCompanyBuilder(1).build();
        final User manager = new MockUserBuilder(1).company(company).role(Role.Manager).build();
        final Department department = new MockDepartmentBuilder(1, company).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertFalse(accessService.hasDepartmentRight(manager, department.getId(), AccessService.DepartmentRight.ADD_PARTICIPANT));
    }

    @Test
    void testThatManagerCantAddParticipantToDepartmentIfItIsNotOwner() {
        final Company company = new MockCompanyBuilder(1).build();
        final User manager = new MockUserBuilder(1).company(company).role(Role.Manager).build();
        final User departmentManager = new MockUserBuilder(2).company(company).role(Role.Manager).build();
        final Department department = new MockDepartmentBuilder(1, company).owner(departmentManager).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertFalse(accessService.hasDepartmentRight(manager, department.getId(), AccessService.DepartmentRight.ADD_PARTICIPANT));
    }

    @Test
    void testThatManagerCanAddParticipantToDepartmentIfItIsNotOwnerButItIsOwnerOfDepartmentOwner() {
        final Company company = new MockCompanyBuilder(1).build();

        final User mainManager = new MockUserBuilder(1).company(company).role(Role.Manager).build();
        final Department managerDepartment = new MockDepartmentBuilder(1, company).owner(mainManager).build();


        final User manager = new MockUserBuilder(2).company(company).departments(managerDepartment).role(Role.Manager).build();
        final Department department = new MockDepartmentBuilder(2, company).owner(manager).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));
        when(relationService.isManager(mainManager, manager)).thenReturn(true);

        assertTrue(accessService.hasDepartmentRight(mainManager, department.getId(), AccessService.DepartmentRight.ADD_PARTICIPANT));
    }

    @Test
    void testThatUserCantAddParticipantToDepartment() {
        final Company company = new MockCompanyBuilder(1).build();
        final User user = new MockUserBuilder(1).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).build();

        when(departmentService.find(eq(department.getId()))).thenReturn(Optional.of(department));

        assertFalse(accessService.hasDepartmentRight(user, department.getId(), AccessService.DepartmentRight.ADD_PARTICIPANT));
    }

    @ParameterizedTest
    @EnumSource(AccessService.WorkSessionRight.class)
    void testThatAdminWithoutCompanyCantDoAnythingWithWorkDay(final AccessService.WorkSessionRight right) {
        final User admin = new MockUserBuilder(1).role(Role.Admin).build();
        assertFalse(accessService.hasWorkSessionRight(admin, null, right));
    }

    @ParameterizedTest
    @EnumSource(AccessService.WorkSessionRight.class)
    void testThatAdminCantDoAnythingWithWorkDayOfUserWithoutCompany(final AccessService.WorkSessionRight right) {
        final Company company = new MockCompanyBuilder(1).build();
        final User admin = new MockUserBuilder(1).company(company).role(Role.Admin).build();
        final User user = new MockUserBuilder(2).build();

        when(userService.find(eq(user.getId()))).thenReturn(Optional.of(user));

        assertFalse(accessService.hasWorkSessionRight(admin, user.getId(), right));
    }

    @ParameterizedTest
    @EnumSource(AccessService.WorkSessionRight.class)
    void testThatAdminCantDoAnythingWithWorkDayOfUserWithAnotherCompany(final AccessService.WorkSessionRight right) {
        final Company adminCompany = new MockCompanyBuilder(1).build();
        final Company userCompany = new MockCompanyBuilder(2).build();
        final User admin = new MockUserBuilder(1).company(adminCompany).role(Role.Admin).build();
        final User user = new MockUserBuilder(2).company(userCompany).build();

        when(userService.find(eq(user.getId()))).thenReturn(Optional.of(user));

        assertFalse(accessService.hasWorkSessionRight(admin, user.getId(), right));
    }

    @ParameterizedTest
    @EnumSource(AccessService.WorkSessionRight.class)
    void testThatAdminCantDoAnythingWithWorkDayOfUserIdCompanyDisabled(final AccessService.WorkSessionRight right) {
        final Company company = new MockCompanyBuilder(1).disabled().build();
        final User admin = new MockUserBuilder(1).company(company).role(Role.Admin).build();
        final User user = new MockUserBuilder(2).company(company).build();

        when(userService.find(eq(user.getId()))).thenReturn(Optional.of(user));

        assertFalse(accessService.hasWorkSessionRight(admin, user.getId(), right));
    }

    @Test
    void testThatAdminCanViewUserWorkDay() {
        final Company company = new MockCompanyBuilder(1).build();
        final User admin = new MockUserBuilder(1).company(company).role(Role.Admin).build();
        final User user = new MockUserBuilder(2).company(company).build();

        when(userService.find(eq(user.getId()))).thenReturn(Optional.of(user));

        assertTrue(accessService.hasWorkSessionRight(admin, user.getId(), AccessService.WorkSessionRight.VIEW));
    }

    @Test
    void testThatManagerCanViewItsUserWorkDay() {
        final Company company = new MockCompanyBuilder(1).build();
        final User manager = new MockUserBuilder(1).company(company).role(Role.Manager).build();
        final User user = new MockUserBuilder(2).company(company).build();

        when(userService.find(eq(user.getId()))).thenReturn(Optional.of(user));
        when(relationService.isManager(eq(manager), eq(user))).thenReturn(true);

        assertTrue(accessService.hasWorkSessionRight(manager, user.getId(), AccessService.WorkSessionRight.VIEW));
    }

    @Test
    void testThatManagerCantViewOtherUserWorkDay() {
        final Company company = new MockCompanyBuilder(1).build();
        final User manager = new MockUserBuilder(1).company(company).role(Role.Manager).build();
        final User user = new MockUserBuilder(2).company(company).build();

        when(userService.find(eq(user.getId()))).thenReturn(Optional.of(user));
        when(relationService.isManager(eq(manager), eq(user))).thenReturn(false);

        assertFalse(accessService.hasWorkSessionRight(manager, user.getId(), AccessService.WorkSessionRight.VIEW));
    }

    @Test
    void testThatUserCanViewOwnWorkDay() {
        final Company company = new MockCompanyBuilder(1).build();
        final User user = new MockUserBuilder(1).company(company).build();

        when(userService.find(eq(user.getId()))).thenReturn(Optional.of(user));

        assertTrue(accessService.hasWorkSessionRight(user, user.getId(), AccessService.WorkSessionRight.VIEW));
    }

    @Test
    void testThatUserCantViewOtherWorkDay() {
        final Company company = new MockCompanyBuilder(1).build();
        final User user1 = new MockUserBuilder(1).company(company).build();
        final User user2 = new MockUserBuilder(2).company(company).build();

        when(userService.find(eq(user2.getId()))).thenReturn(Optional.of(user2));

        assertFalse(accessService.hasWorkSessionRight(user1, user2.getId(), AccessService.WorkSessionRight.VIEW));
    }

    @Test
    void testThatAdminCanCreateUserWorkDay() {
        final Company company = new MockCompanyBuilder(1).build();
        final User admin = new MockUserBuilder(1).company(company).role(Role.Admin).build();
        final User user = new MockUserBuilder(2).company(company).build();

        when(userService.find(eq(user.getId()))).thenReturn(Optional.of(user));

        assertTrue(accessService.hasWorkSessionRight(admin, user.getId(), AccessService.WorkSessionRight.CREATE));
    }

    @Test
    void testThatManagerCanCreateItsUserWorkDay() {
        final Company company = new MockCompanyBuilder(1).build();
        final User manager = new MockUserBuilder(1).company(company).role(Role.Manager).build();
        final User user = new MockUserBuilder(2).company(company).build();

        when(userService.find(eq(user.getId()))).thenReturn(Optional.of(user));
        when(relationService.isManager(eq(manager), eq(user))).thenReturn(true);

        assertTrue(accessService.hasWorkSessionRight(manager, user.getId(), AccessService.WorkSessionRight.CREATE));
    }

    @Test
    void testThatManagerCantCreateOtherUserWorkDay() {
        final Company company = new MockCompanyBuilder(1).build();
        final User manager = new MockUserBuilder(1).company(company).role(Role.Manager).build();
        final User user = new MockUserBuilder(2).company(company).build();

        when(userService.find(eq(user.getId()))).thenReturn(Optional.of(user));
        when(relationService.isManager(eq(manager), eq(user))).thenReturn(false);

        assertFalse(accessService.hasWorkSessionRight(manager, user.getId(), AccessService.WorkSessionRight.CREATE));
    }

    @Test
    void testThatUserCanCreateOwnWorkDay() {
        final Company company = new MockCompanyBuilder(1).build();
        final User user = new MockUserBuilder(1).company(company).build();

        when(userService.find(eq(user.getId()))).thenReturn(Optional.of(user));

        assertTrue(accessService.hasWorkSessionRight(user, user.getId(), AccessService.WorkSessionRight.CREATE));
    }

    @Test
    void testThatUserCantCreateOtherWorkDay() {
        final Company company = new MockCompanyBuilder(1).build();
        final User user1 = new MockUserBuilder(1).company(company).build();
        final User user2 = new MockUserBuilder(2).company(company).build();

        when(userService.find(eq(user2.getId()))).thenReturn(Optional.of(user2));

        assertFalse(accessService.hasWorkSessionRight(user1, user2.getId(), AccessService.WorkSessionRight.CREATE));
    }

    @Test
    void testThatAdminCanEditUserWorkDay() {
        final Company company = new MockCompanyBuilder(1).build();
        final User admin = new MockUserBuilder(1).company(company).role(Role.Admin).build();
        final User user = new MockUserBuilder(2).company(company).build();

        when(userService.find(eq(user.getId()))).thenReturn(Optional.of(user));

        assertTrue(accessService.hasWorkSessionRight(admin, user.getId(), AccessService.WorkSessionRight.EDIT));
    }

    @Test
    void testThatManagerCanEditItsUserWorkDay() {
        final Company company = new MockCompanyBuilder(1).build();
        final User manager = new MockUserBuilder(1).company(company).role(Role.Manager).build();
        final User user = new MockUserBuilder(2).company(company).build();

        when(userService.find(eq(user.getId()))).thenReturn(Optional.of(user));
        when(relationService.isManager(eq(manager), eq(user))).thenReturn(true);

        assertTrue(accessService.hasWorkSessionRight(manager, user.getId(), AccessService.WorkSessionRight.EDIT));
    }

    @Test
    void testThatManagerCantEditOtherUserWorkDay() {
        final Company company = new MockCompanyBuilder(1).build();
        final User manager = new MockUserBuilder(1).company(company).role(Role.Manager).build();
        final User user = new MockUserBuilder(2).company(company).build();

        when(userService.find(eq(user.getId()))).thenReturn(Optional.of(user));
        when(relationService.isManager(eq(manager), eq(user))).thenReturn(false);

        assertFalse(accessService.hasWorkSessionRight(manager, user.getId(), AccessService.WorkSessionRight.EDIT));
    }

    @Test
    void testThatUserCanEditOwnWorkDay() {
        final Company company = new MockCompanyBuilder(1).build();
        final User user = new MockUserBuilder(1).company(company).build();

        when(userService.find(eq(user.getId()))).thenReturn(Optional.of(user));

        assertTrue(accessService.hasWorkSessionRight(user, user.getId(), AccessService.WorkSessionRight.EDIT));
    }

    @Test
    void testThatUserCantEditOtherWorkDay() {
        final Company company = new MockCompanyBuilder(1).build();
        final User user1 = new MockUserBuilder(1).company(company).build();
        final User user2 = new MockUserBuilder(2).company(company).build();

        when(userService.find(eq(user2.getId()))).thenReturn(Optional.of(user2));

        assertFalse(accessService.hasWorkSessionRight(user1, user2.getId(), AccessService.WorkSessionRight.EDIT));
    }

    @Test
    void testThatAdminCanDeleteUserWorkDay() {
        final Company company = new MockCompanyBuilder(1).build();
        final User admin = new MockUserBuilder(1).company(company).role(Role.Admin).build();
        final User user = new MockUserBuilder(2).company(company).build();

        when(userService.find(eq(user.getId()))).thenReturn(Optional.of(user));

        assertTrue(accessService.hasWorkSessionRight(admin, user.getId(), AccessService.WorkSessionRight.DELETE));
    }

    @Test
    void testThatManagerCanDeleteItsUserWorkDay() {
        final Company company = new MockCompanyBuilder(1).build();
        final User manager = new MockUserBuilder(1).company(company).role(Role.Manager).build();
        final User user = new MockUserBuilder(2).company(company).build();

        when(userService.find(eq(user.getId()))).thenReturn(Optional.of(user));
        when(relationService.isManager(eq(manager), eq(user))).thenReturn(true);

        assertTrue(accessService.hasWorkSessionRight(manager, user.getId(), AccessService.WorkSessionRight.DELETE));
    }

    @Test
    void testThatManagerCantDeleteOtherUserWorkDay() {
        final Company company = new MockCompanyBuilder(1).build();
        final User manager = new MockUserBuilder(1).company(company).role(Role.Manager).build();
        final User user = new MockUserBuilder(2).company(company).build();

        when(userService.find(eq(user.getId()))).thenReturn(Optional.of(user));
        when(relationService.isManager(eq(manager), eq(user))).thenReturn(false);

        assertFalse(accessService.hasWorkSessionRight(manager, user.getId(), AccessService.WorkSessionRight.DELETE));
    }

    @Test
    void testThatUserCanDeleteOwnWorkDay() {
        final Company company = new MockCompanyBuilder(1).build();
        final User user = new MockUserBuilder(1).company(company).build();

        when(userService.find(eq(user.getId()))).thenReturn(Optional.of(user));

        assertTrue(accessService.hasWorkSessionRight(user, user.getId(), AccessService.WorkSessionRight.DELETE));
    }

    @Test
    void testThatUserCantDeleteOtherWorkDay() {
        final Company company = new MockCompanyBuilder(1).build();
        final User user1 = new MockUserBuilder(1).company(company).build();
        final User user2 = new MockUserBuilder(2).company(company).build();

        when(userService.find(eq(user2.getId()))).thenReturn(Optional.of(user2));

        assertFalse(accessService.hasWorkSessionRight(user1, user2.getId(), AccessService.WorkSessionRight.DELETE));
    }

    @Test
    void testThatUserCanBeAddedToDepartment() {

        final Company company = new MockCompanyBuilder(1).build();
        final User user = new MockUserBuilder(1).company(company).build();
        final User owner = new MockUserBuilder(2).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).owner(owner).build();

        when(departmentService.find(eq(1))).thenReturn(Optional.of(department));
        when(userService.find(eq(1))).thenReturn(Optional.of(user));
        when(userService.find(eq(2))).thenReturn(Optional.of(owner));
        when(relationService.isManager(eq(user), eq(owner))).thenReturn(false);

        assertTrue(accessService.isUserCanBeAddedToDepartment(1, 1));
    }

    @Test
    void testThatUserWithoutCompanyCantBeAddedToDepartment() {

        final Company company = new MockCompanyBuilder(1).build();
        final User user = new MockUserBuilder(1).build();
        final User owner = new MockUserBuilder(2).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).owner(owner).build();

        when(departmentService.find(eq(1))).thenReturn(Optional.of(department));
        when(userService.find(eq(1))).thenReturn(Optional.of(user));
        when(userService.find(eq(2))).thenReturn(Optional.of(owner));
        when(relationService.isManager(eq(user), eq(owner))).thenReturn(false);

        assertFalse(accessService.isUserCanBeAddedToDepartment(1, 1));
    }

    @Test
    void testThatUserWithDisabledCompanyCantBeAddedToDepartment() {

        final Company company = new MockCompanyBuilder(1).disabled().build();
        final User user = new MockUserBuilder(1).company(company).build();
        final User owner = new MockUserBuilder(2).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).owner(owner).build();

        when(departmentService.find(eq(1))).thenReturn(Optional.of(department));
        when(userService.find(eq(1))).thenReturn(Optional.of(user));
        when(userService.find(eq(2))).thenReturn(Optional.of(owner));
        when(relationService.isManager(eq(user), eq(owner))).thenReturn(false);

        assertFalse(accessService.isUserCanBeAddedToDepartment(1, 1));
    }

    @Test
    void testThatUserFromAnotherCompanyCantBeAddedToDepartment() {

        final Company company = new MockCompanyBuilder(1).build();
        final Company userCompany = new MockCompanyBuilder(2).build();
        final User user = new MockUserBuilder(1).company(userCompany).build();
        final User owner = new MockUserBuilder(2).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).owner(owner).build();

        when(departmentService.find(eq(1))).thenReturn(Optional.of(department));
        when(userService.find(eq(1))).thenReturn(Optional.of(user));
        when(userService.find(eq(2))).thenReturn(Optional.of(owner));
        when(relationService.isManager(eq(user), eq(owner))).thenReturn(false);

        assertFalse(accessService.isUserCanBeAddedToDepartment(1, 1));
    }

    @Test
    void testThatUserCanBeAddedToDepartmentWithoutOwner() {

        final Company company = new MockCompanyBuilder(1).build();
        final User user = new MockUserBuilder(1).company(company).build();
        final User owner = new MockUserBuilder(2).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).build();

        when(departmentService.find(eq(1))).thenReturn(Optional.of(department));
        when(userService.find(eq(1))).thenReturn(Optional.of(user));
        when(userService.find(eq(2))).thenReturn(Optional.of(owner));
        when(relationService.isManager(eq(user), eq(owner))).thenReturn(false);

        assertTrue(accessService.isUserCanBeAddedToDepartment(1, 1));
    }

    @Test
    void testThatUserThatIsManagerOfOwnerCantBeAddedToDepartment() {

        final Company company = new MockCompanyBuilder(1).build();
        final User user = new MockUserBuilder(1).company(company).build();
        final User owner = new MockUserBuilder(2).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).owner(owner).build();

        when(departmentService.find(eq(1))).thenReturn(Optional.of(department));
        when(userService.find(eq(1))).thenReturn(Optional.of(user));
        when(userService.find(eq(2))).thenReturn(Optional.of(owner));
        when(relationService.isManager(eq(user), eq(owner))).thenReturn(true);

        assertFalse(accessService.isUserCanBeAddedToDepartment(1, 1));
    }

    @Test
    void testThatOwnerCanBeAddedToDepartment() {

        final Company company = new MockCompanyBuilder(1).build();
        final User owner = new MockUserBuilder(2).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).owner(owner).build();

        when(departmentService.find(eq(1))).thenReturn(Optional.of(department));
        when(userService.find(eq(2))).thenReturn(Optional.of(owner));

        assertTrue(accessService.isUserCanBeAddedToDepartment(2, 1));
    }

    @Test
    void testThatUserCanBeRemovedFromDepartment() {

        final Company company = new MockCompanyBuilder(1).build();
        final User user = new MockUserBuilder(1).company(company).build();
        final User owner = new MockUserBuilder(2).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).owner(owner).build();

        when(departmentService.find(eq(1))).thenReturn(Optional.of(department));
        when(userService.find(eq(1))).thenReturn(Optional.of(user));
        when(userService.find(eq(2))).thenReturn(Optional.of(owner));

        assertTrue(accessService.isUserCanBeRemovedFromDepartment(1, 1));
    }

    @Test
    void testThatUserWithoutCompanyCantBeRemovedFromDepartment() {

        final Company company = new MockCompanyBuilder(1).build();
        final User user = new MockUserBuilder(1).build();
        final User owner = new MockUserBuilder(2).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).owner(owner).build();

        when(departmentService.find(eq(1))).thenReturn(Optional.of(department));
        when(userService.find(eq(1))).thenReturn(Optional.of(user));
        when(userService.find(eq(2))).thenReturn(Optional.of(owner));

        assertFalse(accessService.isUserCanBeRemovedFromDepartment(1, 1));
    }

    @Test
    void testThatUserWithDisabledCompanyCantBeRemovedFromDepartment() {

        final Company company = new MockCompanyBuilder(1).disabled().build();
        final User user = new MockUserBuilder(1).company(company).build();
        final User owner = new MockUserBuilder(2).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).owner(owner).build();

        when(departmentService.find(eq(1))).thenReturn(Optional.of(department));
        when(userService.find(eq(1))).thenReturn(Optional.of(user));
        when(userService.find(eq(2))).thenReturn(Optional.of(owner));

        assertFalse(accessService.isUserCanBeRemovedFromDepartment(1, 1));
    }

    @Test
    void testThatUserFromAnotherCompanyCantBeRemovedFromDepartment() {

        final Company company = new MockCompanyBuilder(1).build();
        final Company userCompany = new MockCompanyBuilder(2).build();
        final User user = new MockUserBuilder(1).company(userCompany).build();
        final User owner = new MockUserBuilder(2).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).owner(owner).build();

        when(departmentService.find(eq(1))).thenReturn(Optional.of(department));
        when(userService.find(eq(1))).thenReturn(Optional.of(user));
        when(userService.find(eq(2))).thenReturn(Optional.of(owner));

        assertFalse(accessService.isUserCanBeRemovedFromDepartment(1, 1));
    }

    @Test
    void testThatUserCanBeRemovedFromDepartmentWithoutOwner() {

        final Company company = new MockCompanyBuilder(1).build();
        final User user = new MockUserBuilder(1).company(company).build();
        final User owner = new MockUserBuilder(2).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).build();

        when(departmentService.find(eq(1))).thenReturn(Optional.of(department));
        when(userService.find(eq(1))).thenReturn(Optional.of(user));
        when(userService.find(eq(2))).thenReturn(Optional.of(owner));

        assertTrue(accessService.isUserCanBeRemovedFromDepartment(1, 1));
    }

    @Test
    void testThatOwnerCantBeRemovedFromDepartment() {

        final Company company = new MockCompanyBuilder(1).build();
        final User owner = new MockUserBuilder(2).company(company).build();
        final Department department = new MockDepartmentBuilder(1, company).owner(owner).build();

        when(departmentService.find(eq(1))).thenReturn(Optional.of(department));
        when(userService.find(eq(2))).thenReturn(Optional.of(owner));

        assertFalse(accessService.isUserCanBeRemovedFromDepartment(2, 1));
    }

}
