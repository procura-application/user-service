package com.eprocurement.user.users.mapper;


import com.eprocurement.user.users.dto.SignUpRequestDto;
import com.eprocurement.user.users.dto.UserCreationRequestDto;
import com.eprocurement.user.users.dto.UserCreationResponseDto;
import com.eprocurement.user.users.entity.User;
import com.eprocurement.user.users.enums.FunctionalPermissionEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserManagementMapper {

    SignUpRequestDto toSignUpDto(UserCreationRequestDto userCreationRequestDto);

    @Mapping(source = "functionalPermissionEnums",target = "permissionsList")
    @Mapping(source = "buIdList",target = "buIdList")
    UserCreationResponseDto toResponseDto(User user, List<FunctionalPermissionEnum> functionalPermissionEnums,List<String> buIdList);



    User toEntity(UserCreationRequestDto userCreationRequestDto);
}
