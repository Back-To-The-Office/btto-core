package com.btto.core.controller.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import javax.annotation.Nullable;
import java.util.List;

@Value
public class CompanyUsersResponse {
    List<User> users;

    @Value
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class User {
        Integer id;
        String firstName;
        @Nullable
        String lastName;

        public static User fromUser(final com.btto.core.domain.User user) {
            return new User(user.getId(), user.getFirstName(), user.getLastName().orElse(null));
        }
    }
}
