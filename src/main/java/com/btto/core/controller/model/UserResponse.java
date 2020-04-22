package com.btto.core.controller.model;

import com.btto.core.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZoneId;

@SuppressWarnings("FieldCanBeLocal")
@Getter
@AllArgsConstructor
public class UserResponse {
    private final int id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String contacts;
    private final ZoneId timezone;
    private final String position;

    public static UserResponse fromUserDomain(final User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName().orElse(null),
                user.getContacts().orElse(null),
                user.getTimezone(),
                user.getPosition().orElse(null));
    }
}
