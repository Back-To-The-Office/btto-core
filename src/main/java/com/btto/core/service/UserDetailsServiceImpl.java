package com.btto.core.service;

import com.btto.core.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;
    private final AccessService accessService;

    @Autowired
    public UserDetailsServiceImpl(final UserService userService, final AccessService accessService) {
        this.userService = userService;
        this.accessService = accessService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findUserByEmail(username).orElseThrow(() ->
            new UsernameNotFoundException("Can't find user with email " + username)
        );

        if ((!user.getCompany().isPresent() || !user.getCompany().get().isEnabled()) && !accessService.isAdmin(user)) {
            throw new LockedException("The user's company has been deleted, contact support to unblock the user");
        }

        return org.springframework.security.core.userdetails.User.withUsername(username)
                .roles(user.getRole().name())
                .password(user.getPassword())
                .build();
    }
}
