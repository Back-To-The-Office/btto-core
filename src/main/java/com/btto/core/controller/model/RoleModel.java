package com.btto.core.controller.model;

import com.btto.core.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum  RoleModel {

    USER(Role.User), MANAGER(Role.Manager), ADMIN(Role.Admin);

    private final Role domainRole;
}
