package com.eprocurement.user.users.dto;

import com.eprocurement.user.users.enums.UserHierarchyLevelEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDto {

    @NotNull(message = "user name should not be null")
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private UserHierarchyLevelEnum hierarchyLevel;
}
