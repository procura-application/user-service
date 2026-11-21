package com.eprocurement.user.users.controller;

import com.eprocurement.user.common.dto.ApiResponse;
import com.eprocurement.user.users.dto.UserCreationRequestDto;
import com.eprocurement.user.users.dto.UserCreationResponseDto;
import com.eprocurement.user.users.service.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/management/")
@RequiredArgsConstructor
@Slf4j
public class UserManagementController {

    private final UserManagementService userManagementService;


    @PatchMapping("")
    public ResponseEntity<ApiResponse<UserCreationResponseDto>> createOrUpdateBuyer(@Valid @RequestBody UserCreationRequestDto userCreationRequestDto){
        ApiResponse<UserCreationResponseDto> userCreationResponseDto = userManagementService.createOrUpdateBuyer(userCreationRequestDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userCreationResponseDto.getData().getUsername())
                .toUri();
        return ResponseEntity.created(location).body(userCreationResponseDto);
    }
}
