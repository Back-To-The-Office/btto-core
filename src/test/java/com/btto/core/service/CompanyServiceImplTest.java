package com.btto.core.service;

import com.btto.core.dao.CompanyDao;
import com.btto.core.dao.UserDao;
import com.btto.core.domain.Company;
import com.btto.core.domain.User;
import com.btto.core.mock.MockUserBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CompanyServiceImplTest {

    private final UserDao userDao = mock(UserDao.class);
    private final CompanyDao companyDao = mock(CompanyDao.class);

    private final CompanyServiceImpl companyService = new CompanyServiceImpl(companyDao, userDao);

    @Test
    void testCreate() {
        final String name = "testCompany";
        final User creator = new MockUserBuilder(1).email("test@test.com").build();

        final ArgumentCaptor<Company> companyArgumentCaptor = ArgumentCaptor.forClass(Company.class);
        final ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        companyService.create(name, creator);

        verify(companyDao, times(1)).create(companyArgumentCaptor.capture());
        verify(userDao, times(1)).merge(userArgumentCaptor.capture());

        final Company company = companyArgumentCaptor.getValue();
        assertEquals(company.getName(), name);
        assertEquals(company.getUsers().size(), 1);
        assertTrue(company.getUsers().contains(creator));

        final User actualCreator = userArgumentCaptor.getValue();
        assertEquals(actualCreator, creator);
    }

    @Test
    void testDeleteCompany() {
        final Company company = new Company();

        company.setEnabled(true);
        company.setId(1);

        when(companyDao.findOne(eq(1))).thenReturn(company);

        companyService.delete(1);

        assertFalse(company.isEnabled());
    }

    @Test
    void testDeleteUnexcitingCompany() {
        when(companyDao.findOne(eq(1))).thenReturn(null);

        assertThrows(ServiceException.class, () -> companyService.delete(1));
    }

}
