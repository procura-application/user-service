package com.eprocurement.user.users.dto;

import com.eprocurement.user.users.enums.FunctionalPermissionEnum;
import com.eprocurement.user.users.enums.UserHierarchyLevelEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreationRequestDto {


    @NotNull(message = "user name should not be null")
    private String username;

    private String password;
    @Email(message = "Email should be of valid format")
    private String email;
    private String firstName;
    private String lastName;
    private UserHierarchyLevelEnum hierarchyLevel;

    private List<FunctionalPermissionEnum> permissionsList;

    private List<String> businessUnitIdList;
}
