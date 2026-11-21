package com.eprocurement.user.users.repository;

import com.eprocurement.user.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String>{
	
	public Optional<User> findByUsername(String username);
	public Optional<User> findByEmail(String email);

}
