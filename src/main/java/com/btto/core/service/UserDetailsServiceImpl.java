package com.btto.core.service;

import com.btto.core.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    public UserDetailsServiceImpl(@Autowired final UserService userService) {

        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findUserByEmail(username).orElseThrow(() ->
            new UsernameNotFoundException("Can't find user with email " + username)
        );

        return org.springframework.security.core.userdetails.User.withUsername(username)
                .roles(user.getRole().name())
                .password(user.getPassword())
                .build();
    }
}
