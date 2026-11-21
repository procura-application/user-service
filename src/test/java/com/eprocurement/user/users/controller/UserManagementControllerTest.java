package com.eprocurement.user.users.controller;

import com.eprocurement.user.users.dto.UserCreationRequestDto;
import com.eprocurement.user.users.enums.FunctionalPermissionEnum;
import com.eprocurement.user.users.enums.UserHierarchyLevelEnum;
import com.eprocurement.user.users.service.UserManagementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "test")
public class UserManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserManagementService userManagementService;
    @Autowired
    private ObjectMapper objectMapper;

    // we are gojng to use given_when_then naming convention
    @Test
    @Sql(scripts = "/sql/insert-bu.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-bu.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void givenNewUserRequest_whenCreateBuyer_thenCreateNewValidBuyer() throws Exception {
        UserCreationRequestDto userCreationRequestDto = buildUserCreationRequestDto("test","pass@123","test@yopmail.com","test",
                "user",UserHierarchyLevelEnum.EMPLOYEE, Arrays.asList(FunctionalPermissionEnum.PR_BU_VIEW,FunctionalPermissionEnum.PR_CREATION),
                Collections.singletonList("1234")
                );
        mockMvc.perform(MockMvcRequestBuilders.patch("/management/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreationRequestDto))


        ).andExpect(MockMvcResultMatchers.status().isCreated());

    }

    @Test
    @Sql(scripts = "/sql/insert-bu.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-bu.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void givenNewUserInvalidRequest_whenCreateBuyer_thenReturn400() throws Exception {
        UserCreationRequestDto missingUserNameDto = buildUserCreationRequestDto(null,"pass@123","test@yopmail.com","test",
                "user",UserHierarchyLevelEnum.EMPLOYEE, Arrays.asList(FunctionalPermissionEnum.PR_BU_VIEW,FunctionalPermissionEnum.PR_CREATION),
                Collections.singletonList("1234")
        );
        UserCreationRequestDto missingEmailDto = buildUserCreationRequestDto("user","pass@123",null,"test",
                "user",UserHierarchyLevelEnum.EMPLOYEE, Arrays.asList(FunctionalPermissionEnum.PR_BU_VIEW,FunctionalPermissionEnum.PR_CREATION),
                Collections.singletonList("1234")
        );

        UserCreationRequestDto invalidEmailDto = buildUserCreationRequestDto("user","pass@123","test","test",
                "user",UserHierarchyLevelEnum.EMPLOYEE, Arrays.asList(FunctionalPermissionEnum.PR_BU_VIEW,FunctionalPermissionEnum.PR_CREATION),
                Collections.singletonList("1234")
        );
        UserCreationRequestDto missingFirstNameDto = buildUserCreationRequestDto("user","pass@123","test@yopmail.com",null,
                "user",UserHierarchyLevelEnum.EMPLOYEE, Arrays.asList(FunctionalPermissionEnum.PR_BU_VIEW,FunctionalPermissionEnum.PR_CREATION),
                Collections.singletonList("1234")
        );
        UserCreationRequestDto missingLastNameDto = buildUserCreationRequestDto("user","pass@123","test@yopmail.com","test",
                null,UserHierarchyLevelEnum.EMPLOYEE, Arrays.asList(FunctionalPermissionEnum.PR_BU_VIEW,FunctionalPermissionEnum.PR_CREATION),
                Collections.singletonList("1234")
        );
        mockMvc.perform(MockMvcRequestBuilders.patch("/management/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(missingUserNameDto))


        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.patch("/management/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(missingEmailDto))


        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.patch("/management/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidEmailDto))


        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.patch("/management/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(missingFirstNameDto))


        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.patch("/management/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(missingLastNameDto))


        ).andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    @Sql(scripts = "/sql/insert-bu.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/delete-bu.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void givenNewBadUserRequest_whenHierarchyLevelOrPermissionIsInvalid_thenThrow400() throws Exception {
        UserCreationRequestDto userCreationRequestDto = buildUserCreationRequestDto("test","pass@123","test@yopmail.com","test",
                "user",UserHierarchyLevelEnum.EMPLOYEE, Arrays.asList(FunctionalPermissionEnum.PR_BU_VIEW,FunctionalPermissionEnum.PR_CREATION),
                Collections.singletonList("1234")
        );
        Map<String,String> userCreationMap = new HashMap<>();
        userCreationMap.put("username","test");
        userCreationMap.put("email","test@yopmail.com");
        userCreationMap.put("firstName","test");
        userCreationMap.put("lastName","user");
        userCreationMap.put("hierarchyLevel","employe");
        mockMvc.perform(MockMvcRequestBuilders.patch("/management/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreationMap))


        ).andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    public UserCreationRequestDto buildUserCreationRequestDto(String username, String password, String email,
                                                              String firstName, String lastName, UserHierarchyLevelEnum hierarchyLevel,
                                                              List<FunctionalPermissionEnum> permissionsList, List<String> buIdList){
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
}
