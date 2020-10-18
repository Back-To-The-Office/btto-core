package com.btto.core.service;

import com.btto.core.domain.Company;
import com.btto.core.domain.User;
import com.btto.core.domain.enums.Role;
import com.btto.core.mock.MockCompanyBuilder;
import com.btto.core.mock.MockUserBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserDetailsServiceImplTest {

    private final UserService userService = mock(UserService.class);
    private final AccessService accessService = mock(AccessService.class);

    private UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(userService, accessService);

    @Test
    void loadUserByUsername() {
        final Company company = new MockCompanyBuilder(1).build();
        final String email = "test@test.com";
        final String password = "password";
        final User user = new MockUserBuilder(1).company(company).role(Role.User).email(email).password(password).build();

        when(userService.findUserByEmail(eq(email))).thenReturn(Optional.of(user));

        final UserDetails result = userDetailsService.loadUserByUsername(email);

        assertEquals(result.getUsername(), email);
        assertEquals(result.getPassword(), password);
    }

    @Test
    void loadUserByUsernameWhenUserNotFound() {
        final String email = "test@test.com";

        when(userService.findUserByEmail(eq(email))).thenReturn(Optional.empty());

        try {
            userDetailsService.loadUserByUsername(email);
            fail();
        } catch (final UsernameNotFoundException ignored) {
        }
    }

    @Test
    void loadUserByUsernameWhenManagerCompanyHasBeenDisabled() {
        final Company company = new MockCompanyBuilder(1).disabled().build();
        final String email = "test@test.com";
        final String password = "password";
        final User user = new MockUserBuilder(1).company(company).role(Role.Manager).email(email).password(password).build();

        when(userService.findUserByEmail(eq(email))).thenReturn(Optional.of(user));

        try {
            userDetailsService.loadUserByUsername(email);
            fail();
        } catch (final LockedException ignored) {
        }
    }

    @Test
    void loadUserByUsernameWhenManagerDoesntHaveACompany() {
        final String email = "test@test.com";
        final String password = "password";
        final User user = new MockUserBuilder(1).role(Role.Manager).email(email).password(password).build();

        when(userService.findUserByEmail(eq(email))).thenReturn(Optional.of(user));

        try {
            userDetailsService.loadUserByUsername(email);
            fail();
        } catch (final LockedException ignored) {
        }
    }

    @Test
    void loadUserByUsernameWhenAdminCompanyHasBeenDisabled() {
        final Company company = new MockCompanyBuilder(1).disabled().build();
        final String email = "test@test.com";
        final String password = "password";
        final User user = new MockUserBuilder(1).company(company).role(Role.Admin).email(email).password(password).build();

        when(userService.findUserByEmail(eq(email))).thenReturn(Optional.of(user));
        when(accessService.isAdmin(eq(user))).thenReturn(true);

        final UserDetails result = userDetailsService.loadUserByUsername(email);

        assertEquals(result.getUsername(), email);
        assertEquals(result.getPassword(), password);
    }

    @Test
    void loadUserByUsernameWhenAdminDoesntHaveACompany() {
        final String email = "test@test.com";
        final String password = "password";
        final User user = new MockUserBuilder(1).role(Role.Admin).email(email).password(password).build();

        when(userService.findUserByEmail(eq(email))).thenReturn(Optional.of(user));
        when(accessService.isAdmin(eq(user))).thenReturn(true);

        final UserDetails result = userDetailsService.loadUserByUsername(email);

        assertEquals(result.getUsername(), email);
        assertEquals(result.getPassword(), password);
    }
}
