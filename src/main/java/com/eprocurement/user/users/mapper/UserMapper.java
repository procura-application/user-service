package com.eprocurement.user.users.mapper;

import com.eprocurement.user.users.dto.SignUpRequestDto;
import com.eprocurement.user.users.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
	
	User toEntity(SignUpRequestDto signUpRequestDto);

}
