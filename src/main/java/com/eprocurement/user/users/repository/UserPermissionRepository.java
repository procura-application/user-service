package com.eprocurement.user.users.repository;

import com.eprocurement.user.users.entity.User;
import com.eprocurement.user.users.entity.UserPermissionEntity;
import com.eprocurement.user.users.enums.FunctionalPermissionEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermissionEntity,String> {

    List<UserPermissionEntity> findByUser(User user);
    List<UserPermissionEntity> findByUserAndPermissionTypeIn(User user, List<FunctionalPermissionEnum> functionalPermissionEnumList);
}
