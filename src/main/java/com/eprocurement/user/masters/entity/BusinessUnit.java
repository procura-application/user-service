package com.eprocurement.user.masters.entity;


import com.eprocurement.user.common.entity.BaseAuditEntity;
import com.eprocurement.user.common.enums.CommonStatusEnum;
import com.eprocurement.user.users.entity.User;
import com.eprocurement.user.users.entity.UserBuDetailsEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name="master_business_unit",schema = "masters")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessUnit extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name="id")
	private String id;
	@Column(name="business_unit_code",unique = true)
	private String businessUnitCode;
	@Column(name="business_unit_name",unique=true)
	private String businessUnitName;
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="business_unit_manager_id", unique = true)
	private User businessUnitManager;
	@Column(name="business_unit_description")
	private String businessUnitDescription;
	@Column(name="status")
	@Enumerated(EnumType.STRING)
	private CommonStatusEnum status;


//	@OneToMany(mappedBy = "businessUnit",cascade = CascadeType.PERSIST,fetch = FetchType.LAZY)
//	private Set<MasterCategoryEntity> categoryEntitySet;

	@OneToMany(mappedBy = "businessUnit",fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
	private Set<UserBuDetailsEntity> userBuDetailsEntities;
	
}
