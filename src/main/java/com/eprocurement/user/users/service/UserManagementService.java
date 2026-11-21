package com.eprocurement.user.users.service;


import com.eprocurement.user.common.dto.ApiResponse;
import com.eprocurement.user.common.enums.CommonStatusEnum;
import com.eprocurement.user.common.exceptions.DuplicateResourceException;
import com.eprocurement.user.common.exceptions.InvalidResourceException;
import com.eprocurement.user.common.exceptions.NotFoundResourceException;
import com.eprocurement.user.masters.entity.BusinessUnit;
import com.eprocurement.user.masters.repository.BusinessUnitRepository;
import com.eprocurement.user.users.dto.UserCreationRequestDto;
import com.eprocurement.user.users.dto.UserCreationResponseDto;
import com.eprocurement.user.users.entity.User;
import com.eprocurement.user.users.entity.UserBuDetailsEntity;
import com.eprocurement.user.users.entity.UserPermissionEntity;
import com.eprocurement.user.users.enums.FunctionalPermissionEnum;
import com.eprocurement.user.users.enums.UserRoles;
import com.eprocurement.user.users.mapper.UserManagementMapper;
import com.eprocurement.user.users.repository.UserBuDetailsRepository;
import com.eprocurement.user.users.repository.UserPermissionRepository;
import com.eprocurement.user.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserManagementMapper userMapper;
     final String INITIAL_PASSWORD="Test@123";
     private final UserPermissionRepository userPermissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final BusinessUnitRepository businessUnitRepository;
    private final UserBuDetailsRepository userBuDetailsRepository;


    public ApiResponse<UserCreationResponseDto> createOrUpdateBuyer(UserCreationRequestDto userCreationRequestDto){
        User user = userRepository.findByUsername(userCreationRequestDto.getUsername()).isPresent() ?
            updateUser(userCreationRequestDto, UserRoles.BUYER) : saveUser(userCreationRequestDto,UserRoles.BUYER);
        List<FunctionalPermissionEnum> functionalPermissions = saveFunctionalPermissions(user,userCreationRequestDto.getPermissionsList());
        List<String> savedBuIdList = saveUserBuDetails(user,userCreationRequestDto.getBusinessUnitIdList());
        UserCreationResponseDto responseDto = userMapper.toResponseDto(user,functionalPermissions,savedBuIdList);
        return ApiResponse.<UserCreationResponseDto>builder()
                .message("User Created Successfully")
                .data(responseDto)
                .errorList(null)
                .status(HttpStatus.CREATED.value())
        . build();
    }

    private List<FunctionalPermissionEnum> saveFunctionalPermissions(User user,List<FunctionalPermissionEnum> functionalPermissions){
        if(functionalPermissions==null || functionalPermissions.isEmpty()) {
            List<UserPermissionEntity> userPermissionEntities = userPermissionRepository.findByUser(user);
            userPermissionRepository.deleteAll(userPermissionEntities);
            return new ArrayList<>();
        }
        List<FunctionalPermissionEnum> functionalPermissionEnumList = new ArrayList<>(functionalPermissions);
        List<UserPermissionEntity> fetchedUserPermissionEntityList = userPermissionRepository.findByUser(user);
        List<FunctionalPermissionEnum> savedFunctionalPermissions = new ArrayList<>();
        if(fetchedUserPermissionEntityList!=null && !fetchedUserPermissionEntityList.isEmpty()) {
            savedFunctionalPermissions = new ArrayList<>(fetchedUserPermissionEntityList.stream().map(UserPermissionEntity::getPermissionType).toList());
        }
        functionalPermissionEnumList.removeAll(savedFunctionalPermissions);
        List<FunctionalPermissionEnum> toSaveFunctionalPermissions = new ArrayList<>(functionalPermissionEnumList);
        savedFunctionalPermissions.removeAll(functionalPermissions);
        List<FunctionalPermissionEnum> toDeleteFunctionalPermissions = new ArrayList<>(savedFunctionalPermissions);
        List<UserPermissionEntity> toDeleteUserPermissionEntityList = new ArrayList<>(userPermissionRepository.findByUserAndPermissionTypeIn(user,toDeleteFunctionalPermissions));
        userPermissionRepository.deleteAll(toDeleteUserPermissionEntityList);
        List<UserPermissionEntity> userPermissionEntityList = new ArrayList<>();
        for(FunctionalPermissionEnum functionalPermissionEnum: toSaveFunctionalPermissions){
            UserPermissionEntity userPermissionEntity = UserPermissionEntity.builder()
                    .permissionType(functionalPermissionEnum)
                    .status(CommonStatusEnum.ACTIVE)
                    .user(user)
                    .build();
            userPermissionEntityList.add(userPermissionEntity);
        }
        userPermissionRepository.saveAll(userPermissionEntityList);
        return functionalPermissions;

    }

    private List<String> saveUserBuDetails(User user,List<String> businessUnitIdList){
        if(businessUnitIdList==null || businessUnitIdList.isEmpty()) {
            List<UserBuDetailsEntity> userBuDetailsEntities = userBuDetailsRepository.findByUser(user);
            userBuDetailsRepository.deleteAll(userBuDetailsEntities);
            return new ArrayList<>();
        }
        List<String> buIdList = new ArrayList<>(businessUnitIdList);
        List<BusinessUnit> inputBusinessUnitList = businessUnitRepository.findAllById(buIdList);
        List<String> savedBuIdList = new ArrayList<>(inputBusinessUnitList.stream().map(BusinessUnit::getId).toList());
        List<String> notSavedBuIdList = new ArrayList<>(buIdList);
        notSavedBuIdList.removeAll(savedBuIdList);
        if(!notSavedBuIdList.isEmpty()){
            throw new InvalidResourceException("The Provided Business Unit IDs are Invalid");
        }

        List<UserBuDetailsEntity> fetchedBuDetailsEntityList = userBuDetailsRepository.findByUser(user);
        List<String> fetchedBuIdList = new ArrayList<>();
        if(fetchedBuDetailsEntityList!=null && !fetchedBuDetailsEntityList.isEmpty()) {
            fetchedBuIdList = new ArrayList<>(fetchedBuDetailsEntityList.stream().map(UserBuDetailsEntity::getBusinessUnit).toList().stream().map(BusinessUnit::getId).toList());
        }
        savedBuIdList.removeAll(fetchedBuIdList);
        List<String> toSaveBuIdList = new ArrayList<>(savedBuIdList);
        fetchedBuIdList.removeAll(savedBuIdList);
        List<String> toDeleteBuIds = new ArrayList<>(fetchedBuIdList);
        List<UserBuDetailsEntity> toDeleteUserBuDetailsList = new ArrayList<>(userBuDetailsRepository.findByUser_UserIdAndBusinessUnit_IdIn(user.getUserId(),toDeleteBuIds));
        userBuDetailsRepository.deleteAll(toDeleteUserBuDetailsList);
        List<UserBuDetailsEntity> userBuDetailsPermissionList = new ArrayList<>();
        Map<String,BusinessUnit> businessUnitMap = businessUnitRepository.findAllById(toSaveBuIdList).stream()
                .collect(Collectors.toMap(BusinessUnit::getId, Function.identity()));
        for(String buId: toSaveBuIdList){
            UserBuDetailsEntity userBuDetailsEntity =  UserBuDetailsEntity.builder()
                    .status(CommonStatusEnum.ACTIVE)
                    .user(user)
                    .businessUnit(businessUnitMap.get(buId))
                    .build();
            userBuDetailsPermissionList.add(userBuDetailsEntity);
        }
        userBuDetailsRepository.saveAll(userBuDetailsPermissionList);
        return businessUnitIdList;

    }

    public User saveUser(UserCreationRequestDto userCreationRequestDto, UserRoles role){
        if(StringUtils.isEmpty(userCreationRequestDto.getEmail())) throw new IllegalArgumentException("Please Enter Email");
        if(StringUtils.isEmpty(userCreationRequestDto.getFirstName())) throw new IllegalArgumentException("Please Enter First Name");
        if(StringUtils.isEmpty(userCreationRequestDto.getLastName())) throw new IllegalArgumentException("Please Enter Last Name");
        if((userCreationRequestDto.getHierarchyLevel())==null) throw new IllegalArgumentException("Please Enter Hierarchy Level");
        User requestUser = userMapper.toEntity(userCreationRequestDto);
        if(userRepository.findByEmail(userCreationRequestDto.getEmail()).isPresent())
            throw new DuplicateResourceException("Email Already Exists for another user. Please Select a Different Email");
        requestUser.setRole(role);
        requestUser.setPassword(passwordEncoder.encode(INITIAL_PASSWORD));
        requestUser.setStatus(CommonStatusEnum.ACTIVE);
       return userRepository.save(requestUser);

    }

    public User updateUser(UserCreationRequestDto userCreationRequestDto,UserRoles role){
        User fetchedUser = userRepository.findByUsername(userCreationRequestDto.getUsername()).orElseThrow(()-> new NotFoundResourceException("Could not find user of user name:"+userCreationRequestDto.getUsername()));
        if(!StringUtils.isEmpty(userCreationRequestDto.getEmail())){
            if(userRepository.findByEmail(userCreationRequestDto.getEmail()).isPresent() && !fetchedUser.getEmail().equals(userCreationRequestDto.getEmail())) throw new DuplicateResourceException("Email Already Exists. Please Enter Different Email");
            fetchedUser.setEmail(userCreationRequestDto.getEmail());
        }
        if(!StringUtils.isEmpty(userCreationRequestDto.getFirstName())) fetchedUser.setFirstName(userCreationRequestDto.getFirstName());
        if(!StringUtils.isEmpty(userCreationRequestDto.getLastName())) fetchedUser.setLastName(userCreationRequestDto.getLastName());
        if(userCreationRequestDto.getHierarchyLevel()!=null) fetchedUser.setHierarchyLevel(userCreationRequestDto.getHierarchyLevel());

        return userRepository.save(fetchedUser);


    }


}
