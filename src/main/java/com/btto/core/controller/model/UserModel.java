package com.btto.core.controller.model;

import com.btto.core.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@SuppressWarnings("FieldCanBeLocal")
@Getter
@AllArgsConstructor
public class UserModel {
    private final int id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String contacts;

    public static UserModel fromUserDomain(final User user) {
        return new UserModel(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName().orElse(null),
                user.getContacts().orElse(null));
    }
}
