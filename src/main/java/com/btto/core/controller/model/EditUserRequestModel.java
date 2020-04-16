package com.btto.core.controller.model;

import com.btto.core.domain.enums.Role;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Max;
import java.time.ZoneId;

/**
 * json example
 *     "firstName":"Sergey"
 *     "lastName":"Efimov"
 *     "newPassword":"1234"
 *     "oldPassword":"4321"
 *     "timezone":"UTC+3"
 *     "role":"USER"
 *     "position":"Senior Back-end developer"
 */
@SuppressWarnings("unused")
@Data
public class EditUserRequestModel {
    @Nullable
    @Max(255)
    private String firstName;
    @Nullable
    @Max(255)
    private String lastName;
    @Nullable
    private String oldPassword;
    @Nullable
    private String newPassword;
    @Nullable
    private ZoneId timezone;
    @Nullable
    private RoleModel role;
    @Nullable
    @Max(255)
    private String position;

    @Nullable
    public Role getDomainRole() {
        return role == null ? null : role.getDomainRole();
    }

    @AssertTrue(message = "Please set both old and new passwords")
    public boolean isPasswordFieldsValid() {
        final boolean isOldPasswordBlank = StringUtils.isBlank(oldPassword);
        final boolean isNewPasswordBlank = StringUtils.isBlank(newPassword);
        if ( !isOldPasswordBlank || !isNewPasswordBlank) {
            return !isOldPasswordBlank && !isNewPasswordBlank;
        }
        return true;
    }
}
