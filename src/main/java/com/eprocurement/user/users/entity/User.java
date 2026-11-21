package com.eprocurement.user.users.entity;

import com.eprocurement.user.common.entity.BaseAuditEntity;
import com.eprocurement.user.common.enums.CommonStatusEnum;
import com.eprocurement.user.users.enums.UserHierarchyLevelEnum;
import com.eprocurement.user.users.enums.UserRoles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Entity
@Table(name = "user_details",schema="masters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseAuditEntity implements UserDetails{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(name ="user_id")
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String userId;
	@Column(name="user_name",unique = true)
	private String username;
	@Column(name="password")
	private String password;
	@Column(name="email",unique = true)
	private String email;
	@Column(name="role")
	@Enumerated(EnumType.STRING)
	private UserRoles role;

	@Column(name = "first_name")
	private String firstName;
	@Column(name="last_name")
	private String lastName;

	@Column(name = "hierarchy_level")
	@Enumerated(EnumType.STRING)
	private UserHierarchyLevelEnum hierarchyLevel;
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private CommonStatusEnum status;
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return role.getAuthorities();
	}


}
