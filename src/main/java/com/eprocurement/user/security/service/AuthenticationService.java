package com.eprocurement.user.security.service;


import com.eprocurement.user.common.dto.ApiResponse;
import com.eprocurement.user.common.enums.CommonStatusEnum;
import com.eprocurement.user.users.dto.AuthenticationRequest;
import com.eprocurement.user.users.dto.AuthenticationResponse;
import com.eprocurement.user.users.dto.SignUpRequestDto;
import com.eprocurement.user.users.entity.User;
import com.eprocurement.user.users.enums.UserRoles;
import com.eprocurement.user.users.exceptions.UserAlreadyExistsException;
import com.eprocurement.user.users.mapper.UserMapper;
import com.eprocurement.user.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

	private final UserMapper userMapper;
	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	
	public ResponseEntity<ApiResponse<AuthenticationResponse>> register(SignUpRequestDto signUpRequestDto, UserRoles role){
		AuthenticationResponse authenticationResponse = null;
		User requestUser = saveUser(signUpRequestDto,role);
			String jwtToken = jwtService.buildToken(requestUser, requestUser.getRole(), requestUser.getUserId());
			authenticationResponse = AuthenticationResponse.builder()
					.jwtToken(jwtToken).build();

		
		return ResponseEntity.ok().body(ApiResponse.<AuthenticationResponse>builder().data(authenticationResponse)
				.message("User Registration successful")
				.errorList(null)
				.status(HttpStatus.OK.value())
				.build()
				);
				
		
	}
	
	public ResponseEntity<ApiResponse<AuthenticationResponse>> login(AuthenticationRequest authenticationRequest){
		AuthenticationResponse authenticationResponse = null;
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),authenticationRequest.getPassword()));
        User user = userRepository.findByUsername(authenticationRequest.getUsername()).orElseThrow(()-> new UsernameNotFoundException("Could not find user of user name:"+authenticationRequest.getUsername()));
        String jwtToken = jwtService.buildToken(user, user.getRole(),user.getUserId());
		authenticationResponse = AuthenticationResponse.builder()
				.jwtToken(jwtToken).build();
		return ResponseEntity.ok().body(ApiResponse.<AuthenticationResponse>builder().data(authenticationResponse)
				.message("Login Successfull")
				.errorList(null)
				.status(HttpStatus.OK.value())
				.build()
				);
        
	}
	
	
	public boolean checkIfUserExists(User requestUser) {
		return userRepository.findByUsername(requestUser.getUsername()).isPresent();
	}

	public User saveUser(SignUpRequestDto signUpRequestDto,UserRoles role){
		User requestUser = userMapper.toEntity(signUpRequestDto);

		if(checkIfUserExists(requestUser)) throw new UserAlreadyExistsException("User Already Exists");
		else {
			requestUser.setRole(role);
			requestUser.setPassword(passwordEncoder.encode(requestUser.getPassword()));
			requestUser.setStatus(CommonStatusEnum.ACTIVE);
			userRepository.save(requestUser);
		}
		return requestUser;
	}


}
