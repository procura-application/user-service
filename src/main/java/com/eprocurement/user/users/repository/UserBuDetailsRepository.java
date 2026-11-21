package com.eprocurement.user.users.repository;

import com.eprocurement.user.users.entity.User;
import com.eprocurement.user.users.entity.UserBuDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBuDetailsRepository extends JpaRepository<UserBuDetailsEntity,String> {

    List<UserBuDetailsEntity> findByUser(User user);
    List<UserBuDetailsEntity> findByUser_UserIdAndBusinessUnit_IdIn(String userId,List<String> businessUnitId);
}
