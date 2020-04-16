package com.btto.core.controller.model;

import com.btto.core.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZoneId;

@SuppressWarnings("FieldCanBeLocal")
@Getter
@AllArgsConstructor
public class UserResponseModel {
    private final int id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String contacts;
    private final ZoneId timezone;
    private final String position;

    public static UserResponseModel fromUserDomain(final User user) {
        return new UserResponseModel(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName().orElse(null),
                user.getContacts().orElse(null),
                user.getTimezone(),
                user.getPosition().orElse(null));
    }
}
