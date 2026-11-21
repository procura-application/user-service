package com.eprocurement.user.users.dto;

import com.eprocurement.user.users.enums.UserHierarchyLevelEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequestDto {

	@NotNull(message = "user name should not be null")
	private String username;
	@NotNull(message = "password should not be null")
	private String password;
	@Email(message = "Email should be of valid format")
	private String email;
	@NotNull(message = "first name should not be null")
	private String firstName;
	@NotNull(message = "last name should not be null")
	private String lastName;
	@NotNull(message = "hierarchy level should not be null")
	private UserHierarchyLevelEnum hierarchyLevel;
	

}
