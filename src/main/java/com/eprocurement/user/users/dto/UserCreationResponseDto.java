package com.eprocurement.user.users.dto;


import com.eprocurement.user.users.enums.FunctionalPermissionEnum;
import com.eprocurement.user.users.enums.UserHierarchyLevelEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreationResponseDto {

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private UserHierarchyLevelEnum hierarchyLevel;
    private List<FunctionalPermissionEnum> permissionsList;
    private List<String> buIdList;
}
