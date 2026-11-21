package com.eprocurement.user.users.service;


import com.eprocurement.user.common.enums.CommonStatusEnum;
import com.eprocurement.user.common.exceptions.DuplicateResourceException;
import com.eprocurement.user.masters.entity.BusinessUnit;
import com.eprocurement.user.masters.repository.BusinessUnitRepository;
import com.eprocurement.user.users.dto.UserCreationRequestDto;
import com.eprocurement.user.users.dto.UserCreationResponseDto;
import com.eprocurement.user.users.entity.User;
import com.eprocurement.user.users.entity.UserBuDetailsEntity;
import com.eprocurement.user.users.entity.UserPermissionEntity;
import com.eprocurement.user.users.enums.FunctionalPermissionEnum;
import com.eprocurement.user.users.enums.UserHierarchyLevelEnum;
import com.eprocurement.user.users.enums.UserRoles;
import com.eprocurement.user.users.repository.UserBuDetailsRepository;
import com.eprocurement.user.users.repository.UserPermissionRepository;
import com.eprocurement.user.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@SpringBootTest
@WithMockUser(username = "test")
public class UserManagementServiceTest {


    final String INITIAL_PASSWORD="Test@123";
    @Autowired
    private UserPermissionRepository userPermissionRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private  UserManagementService userManagementService;
    @Autowired
    private BusinessUnitRepository businessUnitRepository;
    @Autowired
    private UserBuDetailsRepository userBuDetailsRepository;

    @AfterEach
    public void deleteAllDataForTests(){
        userBuDetailsRepository.deleteAll();
        businessUnitRepository.deleteAll();
        userPermissionRepository.deleteAll();
        userRepository.deleteAll();
    }
    // we are gojng to use given_when_then naming convention
    @Test
    public void givenNewUserRequest_whenCreateBuyer_thenCreateNewValidBuyer(){
        BusinessUnit businessUnit = buildBusinessUnitEntity("BU_TEST","test-bu","this is test bu");
        BusinessUnit savedBu = businessUnitRepository.save(businessUnit);
        UserCreationRequestDto userCreationRequestDto = buildUserCreationRequestDto("test",INITIAL_PASSWORD,
                "test@yopmail.com","test","user", UserHierarchyLevelEnum.EMPLOYEE
                ,Arrays.asList(FunctionalPermissionEnum.PR_CREATION,FunctionalPermissionEnum.PR_BU_VIEW), Collections.singletonList(savedBu.getId()));
       UserCreationResponseDto userResponse =  userManagementService.createOrUpdateBuyer(userCreationRequestDto).getData();
       Assertions.assertTrue(userRepository.findByUsername(userCreationRequestDto.getUsername()).isPresent());
       User user = userRepository.findByUsername(userCreationRequestDto.getUsername()).orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
       Assertions.assertNotEquals(userRepository.findByUsername(userCreationRequestDto.getUsername()).get().getPassword(),userCreationRequestDto.getPassword());
        Assertions.assertFalse(userPermissionRepository.findByUser(user).isEmpty());
        Assertions.assertEquals(userResponse.getFirstName(),userCreationRequestDto.getFirstName());
        Assertions.assertEquals(userResponse.getLastName(),userCreationRequestDto.getLastName());
        Assertions.assertEquals(userResponse.getUsername(),userCreationRequestDto.getUsername());
        Assertions.assertEquals(userResponse.getEmail(),userCreationRequestDto.getEmail());
        Assertions.assertEquals(userResponse.getPermissionsList(),Arrays.asList(FunctionalPermissionEnum.PR_CREATION,FunctionalPermissionEnum.PR_BU_VIEW));
        Assertions.assertEquals(userResponse.getBuIdList(),userCreationRequestDto.getBusinessUnitIdList());

    }

    @Test
    public void givenNewUserRequestAndOldUser_whenCreateBuyerWithSameEmail_thenThrowDuplicateResourceException(){
            User user = buildUserEntity("test","password","test@yopmail.com","test","user",UserHierarchyLevelEnum.EMPLOYEE);
            userRepository.save(user);
            UserPermissionEntity userPermissionEntity = buildUserPermissionEntity(FunctionalPermissionEnum.PR_BU_VIEW,user);
            userPermissionRepository.save(userPermissionEntity);
        UserCreationRequestDto userCreationRequestDto = buildUserCreationRequestDto("test2",INITIAL_PASSWORD,
                "test@yopmail.com","test2","user2", UserHierarchyLevelEnum.EMPLOYEE,
                Arrays.asList(FunctionalPermissionEnum.PR_CREATION,FunctionalPermissionEnum.PR_BU_VIEW),null);
Assertions.assertThrows(DuplicateResourceException.class,() -> userManagementService.createOrUpdateBuyer(userCreationRequestDto));

    }

    @Test
    @Transactional
    public void givenUpdateUserRequestAndOldUser_whenUpdateBuyerWithSameEmailOfOldUser_thenThrowDuplicateResourceException(){
        User existingUser = buildUserEntity("test","password","test@yopmail.com","test","user",UserHierarchyLevelEnum.EMPLOYEE);
        userRepository.save(existingUser);
        UserPermissionEntity userPermissionEntity = buildUserPermissionEntity(FunctionalPermissionEnum.PR_BU_VIEW,existingUser);
        userPermissionRepository.save(userPermissionEntity);
        User oldUser = buildUserEntity("test2","password","test2@yopmail.com","test","user",UserHierarchyLevelEnum.EMPLOYEE);
        userRepository.save(oldUser);
        UserPermissionEntity oldUserPermissionEntity = buildUserPermissionEntity(FunctionalPermissionEnum.PR_BU_VIEW,oldUser);
        userPermissionRepository.save(oldUserPermissionEntity);
        UserCreationRequestDto userCreationRequestDto = buildUserCreationRequestDto("test2",INITIAL_PASSWORD,
                "test@yopmail.com","test2","user2", UserHierarchyLevelEnum.EMPLOYEE,
                Arrays.asList(FunctionalPermissionEnum.PR_CREATION,FunctionalPermissionEnum.PR_BU_VIEW),null);
        Assertions.assertThrows(DuplicateResourceException.class,() -> userManagementService.createOrUpdateBuyer(userCreationRequestDto));

    }

    @Test
    @Transactional
    public void givenUpdateUserRequestAndOldUser_whenUpdateBuyerWithSameEmailOfSameUser_thenUpdateBuyer(){
        User existingUser = buildUserEntity("test","password","test@yopmail.com","test","user",UserHierarchyLevelEnum.EMPLOYEE);
        userRepository.save(existingUser);
        UserPermissionEntity userPermissionEntity = buildUserPermissionEntity(FunctionalPermissionEnum.PR_BU_VIEW,existingUser);
        userPermissionRepository.save(userPermissionEntity);
        User oldUser = buildUserEntity("test2","password","test2@yopmail.com","test","user",UserHierarchyLevelEnum.EMPLOYEE);
        userRepository.save(oldUser);
        UserPermissionEntity oldUserPermissionEntity = buildUserPermissionEntity(FunctionalPermissionEnum.PR_BU_VIEW,oldUser);
        userPermissionRepository.save(oldUserPermissionEntity);
        UserCreationRequestDto userCreationRequestDto = buildUserCreationRequestDto("test2",INITIAL_PASSWORD,
                "test2@yopmail.com","test2","user2", UserHierarchyLevelEnum.EMPLOYEE,
                Arrays.asList(FunctionalPermissionEnum.PR_CREATION,FunctionalPermissionEnum.PR_BU_VIEW),null);
        UserCreationResponseDto userCreationResponseDto = userManagementService.createOrUpdateBuyer(userCreationRequestDto).getData();
        Assertions.assertEquals("test2@yopmail.com", userCreationResponseDto.getEmail());
        Assertions.assertEquals(oldUser.getUsername(),userCreationResponseDto.getUsername());

    }




    @Test
    @Transactional
    public void givenUpdateUserRequestAndOldUser_whenUpdateBuyerToAddPermissions_thenUpdateBuyerWithOldAndNewPermissions(){

        User user = buildUserEntity("test",INITIAL_PASSWORD,"test@yopmail.com","test","user",UserHierarchyLevelEnum.EMPLOYEE);
        userRepository.save(user);
        UserPermissionEntity userPermissionEntity= buildUserPermissionEntity(FunctionalPermissionEnum.PR_BU_VIEW,user);
        userPermissionRepository.save(userPermissionEntity);
        UserCreationRequestDto userCreationRequestDto = buildUserCreationRequestDto("test",null,"test@yopmail.com","test","user",UserHierarchyLevelEnum.EMPLOYEE
                ,Arrays.asList(FunctionalPermissionEnum.PR_CREATION,FunctionalPermissionEnum.PR_BU_VIEW),null);
        UserCreationResponseDto userCreationResponseDto = userManagementService.createOrUpdateBuyer(userCreationRequestDto).getData();
        Assertions.assertEquals(userCreationResponseDto.getPermissionsList(),Arrays.asList(FunctionalPermissionEnum.PR_CREATION,FunctionalPermissionEnum.PR_BU_VIEW));
        List<UserPermissionEntity> userPermissionEntities = userPermissionRepository.findByUser(user);
        List<FunctionalPermissionEnum> permissions = Arrays.asList(FunctionalPermissionEnum.PR_CREATION,FunctionalPermissionEnum.PR_BU_VIEW);
        for(UserPermissionEntity userPermission : userPermissionEntities){
            Assertions.assertTrue(permissions.contains(userPermission.getPermissionType()));
            Assertions.assertEquals(userPermission.getUser().getUsername(),user.getUsername());
        }

    }



    @Test
    @Transactional
    public void givenUpdateUserRequestAndOldUser_whenUpdateBuyerToRemovePermissions_thenRemoveAllPermissions(){

        User user = buildUserEntity("test",INITIAL_PASSWORD,"test@yopmail.com","test","user",UserHierarchyLevelEnum.EMPLOYEE);
        userRepository.save(user);
        UserPermissionEntity userPermissionEntity= buildUserPermissionEntity(FunctionalPermissionEnum.PR_BU_VIEW,user);
        userPermissionRepository.save(userPermissionEntity);
        UserCreationRequestDto userCreationRequestDto = buildUserCreationRequestDto("test",null,"test@yopmail.com","test","user",UserHierarchyLevelEnum.EMPLOYEE
                ,new ArrayList<>(),null);
        UserCreationResponseDto userCreationResponseDto = userManagementService.createOrUpdateBuyer(userCreationRequestDto).getData();
        Assertions.assertEquals(new ArrayList<>(), userCreationResponseDto.getPermissionsList());
        List<UserPermissionEntity> userPermissionEntities = userPermissionRepository.findByUser(user);
      Assertions.assertTrue(userPermissionEntities.isEmpty());

    }

    @Test
    @Transactional
    public void givenUpdateUserRequestAndOldUser_whenUpdateBuyerToRemovePermissionsIsNull_thenRemoveAllPermissions(){

        User user = buildUserEntity("test",INITIAL_PASSWORD,"test@yopmail.com","test","user",UserHierarchyLevelEnum.EMPLOYEE);
        userRepository.save(user);
        UserPermissionEntity userPermissionEntity= buildUserPermissionEntity(FunctionalPermissionEnum.PR_BU_VIEW,user);
        userPermissionRepository.save(userPermissionEntity);
        UserCreationRequestDto userCreationRequestDto = buildUserCreationRequestDto("test",null,"test@yopmail.com","test","user",UserHierarchyLevelEnum.EMPLOYEE
                ,null,null);
        UserCreationResponseDto userCreationResponseDto = userManagementService.createOrUpdateBuyer(userCreationRequestDto).getData();
        Assertions.assertEquals(new ArrayList<>(), userCreationResponseDto.getPermissionsList());
        List<UserPermissionEntity> userPermissionEntities = userPermissionRepository.findByUser(user);
        Assertions.assertTrue(userPermissionEntities.isEmpty());

    }


    @Test
    @Transactional
    public void givenUpdateUserRequestAndOldUser_whenUpdateBuyerToAddAndRemovePermissions_thenUpdateBuyerWithOldAndNewPermissions(){

        User user = buildUserEntity("test",INITIAL_PASSWORD,"test@yopmail.com","test","user",UserHierarchyLevelEnum.EMPLOYEE);
        userRepository.save(user);
        UserPermissionEntity userPermissionEntity= buildUserPermissionEntity(FunctionalPermissionEnum.PR_BU_VIEW,user);
        userPermissionRepository.save(userPermissionEntity);
        UserPermissionEntity userPermissionEntity2= buildUserPermissionEntity(FunctionalPermissionEnum.PR_CREATION,user);
        userPermissionRepository.save(userPermissionEntity2);

        UserCreationRequestDto userCreationRequestDto = buildUserCreationRequestDto("test",null,"test@yopmail.com","test","user",UserHierarchyLevelEnum.EMPLOYEE
                ,Arrays.asList(FunctionalPermissionEnum.PR_APPROVAL,FunctionalPermissionEnum.PR_BU_VIEW),null);
        UserCreationResponseDto userCreationResponseDto = userManagementService.createOrUpdateBuyer(userCreationRequestDto).getData();
        Assertions.assertEquals(userCreationResponseDto.getPermissionsList(),Arrays.asList(FunctionalPermissionEnum.PR_APPROVAL,FunctionalPermissionEnum.PR_BU_VIEW));
        List<UserPermissionEntity> userPermissionEntities = userPermissionRepository.findByUser(user);
        List<FunctionalPermissionEnum> permissions = Arrays.asList(FunctionalPermissionEnum.PR_APPROVAL,FunctionalPermissionEnum.PR_BU_VIEW);
        for(UserPermissionEntity userPermission : userPermissionEntities){
            Assertions.assertTrue(permissions.contains(userPermission.getPermissionType()));
            Assertions.assertEquals(userPermission.getUser().getUsername(),user.getUsername());
        }

    }



    @Test
    @Transactional
    public void givenUpdateUserRequestAndOldUser_whenUpdateBuyerToAddBu_thenUpdateBuyerWithOldAndNewBu(){

        User user = buildUserEntity("test",INITIAL_PASSWORD,"test@yopmail.com","test","user",UserHierarchyLevelEnum.EMPLOYEE);
        userRepository.save(user);
        UserPermissionEntity userPermissionEntity= buildUserPermissionEntity(FunctionalPermissionEnum.PR_BU_VIEW,user);
        userPermissionRepository.save(userPermissionEntity);
        BusinessUnit businessUnit = buildBusinessUnitEntity("BU_TEST","Test Bu","This is a test bu");
        BusinessUnit savedBu = businessUnitRepository.save(businessUnit);
        UserBuDetailsEntity userBuDetailsEntity = buildUserBuDetailsEntity(user,savedBu);
        userBuDetailsRepository.save(userBuDetailsEntity);
        BusinessUnit testBusinessUnit = buildBusinessUnitEntity("BU_TEST1","Test Bu 1","This is a test bu 1");
        BusinessUnit testSavedBu = businessUnitRepository.save(testBusinessUnit);
        UserCreationRequestDto userCreationRequestDto = buildUserCreationRequestDto("test",null,"test@yopmail.com","test","user",UserHierarchyLevelEnum.EMPLOYEE
                ,Arrays.asList(FunctionalPermissionEnum.PR_CREATION,FunctionalPermissionEnum.PR_BU_VIEW),Arrays.asList(savedBu.getId(),testSavedBu.getId()));
        UserCreationResponseDto userCreationResponseDto = userManagementService.createOrUpdateBuyer(userCreationRequestDto).getData();
        Assertions.assertEquals(userCreationResponseDto.getBuIdList(),Arrays.asList(savedBu.getId(),testSavedBu.getId()));
        List<UserBuDetailsEntity> userBuDetailsEntities = userBuDetailsRepository.findByUser(user);
        List<String> buIds = Arrays.asList(savedBu.getId(),testSavedBu.getId());
        for(UserBuDetailsEntity buDetailsEntity : userBuDetailsEntities){
            Assertions.assertTrue(buIds.contains(buDetailsEntity.getBusinessUnit().getId()));
            Assertions.assertEquals(buDetailsEntity.getUser().getUsername(),user.getUsername());
        }

    }



    @Test
    @Transactional
    public void givenUpdateUserRequestAndOldUser_whenUpdateBuyerToRemoveBu_thenRemoveAllBu(){

        User user = buildUserEntity("test",INITIAL_PASSWORD,"test@yopmail.com","test","user",UserHierarchyLevelEnum.EMPLOYEE);
        userRepository.save(user);
        UserPermissionEntity userPermissionEntity= buildUserPermissionEntity(FunctionalPermissionEnum.PR_BU_VIEW,user);
        userPermissionRepository.save(userPermissionEntity);
        BusinessUnit businessUnit = buildBusinessUnitEntity("test-bu","test bu","test bu");
        BusinessUnit savedBu = businessUnitRepository.save(businessUnit);
        UserBuDetailsEntity userBuDetails = buildUserBuDetailsEntity(user,businessUnit);
        userBuDetailsRepository.save(userBuDetails);
        Assertions.assertFalse(userBuDetailsRepository.findByUser(user).isEmpty());
        UserCreationRequestDto userCreationRequestDto = buildUserCreationRequestDto("test",null,"test@yopmail.com","test","user",UserHierarchyLevelEnum.EMPLOYEE
                ,new ArrayList<>(),new ArrayList<>());
        UserCreationResponseDto userCreationResponseDto = userManagementService.createOrUpdateBuyer(userCreationRequestDto).getData();
        Assertions.assertEquals(new ArrayList<>(), userCreationResponseDto.getBuIdList());
        List<UserBuDetailsEntity> userBuDetailsEntities = userBuDetailsRepository.findByUser(user);
        Assertions.assertTrue(userBuDetailsEntities.isEmpty());

    }

    @Test
    @Transactional
    public void givenUpdateUserRequestAndOldUser_whenUpdateBuyerToRemoveBuIsNull_thenRemoveAllBu(){

        User user = buildUserEntity("test",INITIAL_PASSWORD,"test@yopmail.com","test","user",UserHierarchyLevelEnum.EMPLOYEE);
        userRepository.save(user);
        UserPermissionEntity userPermissionEntity= buildUserPermissionEntity(FunctionalPermissionEnum.PR_BU_VIEW,user);
        userPermissionRepository.save(userPermissionEntity);
        BusinessUnit businessUnit = buildBusinessUnitEntity("test-bu","test bu","test bu");
        BusinessUnit savedBu = businessUnitRepository.save(businessUnit);
        UserBuDetailsEntity userBuDetails = buildUserBuDetailsEntity(user,businessUnit);
        userBuDetailsRepository.save(userBuDetails);
        UserCreationRequestDto userCreationRequestDto = buildUserCreationRequestDto("test",null,"test@yopmail.com","test","user",UserHierarchyLevelEnum.EMPLOYEE
                ,null,null);
        UserCreationResponseDto userCreationResponseDto = userManagementService.createOrUpdateBuyer(userCreationRequestDto).getData();
        Assertions.assertEquals(new ArrayList<>(), userCreationResponseDto.getBuIdList());
        List<UserBuDetailsEntity> userBuDetailsEntities = userBuDetailsRepository.findByUser(user);
        Assertions.assertTrue(userBuDetailsEntities.isEmpty());

    }


    @Test
    @Transactional
    public void givenUpdateUserRequestAndOldUser_whenUpdateBuyerToAddAndRemoveBu_thenUpdateBuyerWithOldAndBu(){

        User user = buildUserEntity("test",INITIAL_PASSWORD,"test@yopmail.com","test","user",UserHierarchyLevelEnum.EMPLOYEE);
        userRepository.save(user);
        UserPermissionEntity userPermissionEntity= buildUserPermissionEntity(FunctionalPermissionEnum.PR_BU_VIEW,user);
        userPermissionRepository.save(userPermissionEntity);
        UserPermissionEntity userPermissionEntity2= buildUserPermissionEntity(FunctionalPermissionEnum.PR_CREATION,user);
        userPermissionRepository.save(userPermissionEntity2);
        BusinessUnit businessUnit = buildBusinessUnitEntity("test-bu","test bu","test bu");
        BusinessUnit savedBu = businessUnitRepository.save(businessUnit);
        UserBuDetailsEntity userBuDetails = buildUserBuDetailsEntity(user,businessUnit);
        userBuDetailsRepository.save(userBuDetails);
        BusinessUnit businessUnit2 = buildBusinessUnitEntity("test-bu2","test bu2","test bu2");
        BusinessUnit savedBu2 = businessUnitRepository.save(businessUnit2);
        UserBuDetailsEntity userBuDetails2 = buildUserBuDetailsEntity(user,businessUnit2);
        userBuDetailsRepository.save(userBuDetails2);
        BusinessUnit businessUnit3 = buildBusinessUnitEntity("test-bu3","test bu3","test bu3");
        BusinessUnit savedBu3 = businessUnitRepository.save(businessUnit3);
        UserCreationRequestDto userCreationRequestDto = buildUserCreationRequestDto("test",null,"test@yopmail.com","test","user",UserHierarchyLevelEnum.EMPLOYEE
                ,Arrays.asList(FunctionalPermissionEnum.PR_APPROVAL,FunctionalPermissionEnum.PR_BU_VIEW),
                Arrays.asList(savedBu2.getId(),savedBu3.getId())
                );
        UserCreationResponseDto userCreationResponseDto = userManagementService.createOrUpdateBuyer(userCreationRequestDto).getData();
        List<String> buIdCheckList = Arrays.asList(savedBu2.getId(),savedBu3.getId());

        Assertions.assertEquals(userCreationResponseDto.getBuIdList(),buIdCheckList);
        List<UserBuDetailsEntity> userBuDetailsEntities = userBuDetailsRepository.findByUser(user);
        for(UserBuDetailsEntity userBuDetailsEntity : userBuDetailsEntities){
            Assertions.assertTrue(buIdCheckList.contains(userBuDetailsEntity.getBusinessUnit().getId()));
            Assertions.assertEquals(userBuDetailsEntity.getUser().getUsername(),user.getUsername());
        }

    }





    public UserCreationRequestDto buildUserCreationRequestDto(String username,String password,String email,
                                                              String firstName,String lastName,UserHierarchyLevelEnum hierarchyLevel,
                                                              List<FunctionalPermissionEnum> permissionsList,List<String> buIdList){
        return UserCreationRequestDto.builder()
                .username(username)
                .password(password)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .hierarchyLevel(hierarchyLevel)
                .permissionsList(permissionsList)
                .businessUnitIdList(buIdList)
                .build();
    }

    public User buildUserEntity(String username,String password,String email,
                                                              String firstName,String lastName,UserHierarchyLevelEnum hierarchyLevel){
        return User.builder()
                .username(username)
                .password(password)
                .email(email)
                .role(UserRoles.BUYER)
                .firstName(firstName)
                .lastName(lastName)
                .hierarchyLevel(hierarchyLevel)
                .status(CommonStatusEnum.ACTIVE)
                .build();
    }

    public UserPermissionEntity buildUserPermissionEntity(FunctionalPermissionEnum functionalPermissionEnum,User user){
        return UserPermissionEntity.builder()
                .user(user)
                .permissionType(functionalPermissionEnum)
                .status(CommonStatusEnum.ACTIVE)
                .build();
    }

    public BusinessUnit buildBusinessUnitEntity(String buCode,String buName,String buDescription){
        return BusinessUnit.builder()
                .businessUnitCode(buCode)
                .businessUnitName(buName)
                .businessUnitDescription(buDescription)
                .status(CommonStatusEnum.ACTIVE)
                .build();
    }

    public UserBuDetailsEntity buildUserBuDetailsEntity(User user,BusinessUnit businessUnit){
        return UserBuDetailsEntity.builder()
                .businessUnit(businessUnit)
                .user(user)
                .status(CommonStatusEnum.ACTIVE)
                .build();
    }

}
