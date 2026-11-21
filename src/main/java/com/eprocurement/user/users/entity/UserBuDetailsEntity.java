package com.eprocurement.user.users.entity;


import com.eprocurement.user.common.entity.BaseAuditEntity;
import com.eprocurement.user.common.enums.CommonStatusEnum;
import com.eprocurement.user.masters.entity.BusinessUnit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_bu_details",schema = "masters")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBuDetailsEntity extends BaseAuditEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_bu_details_id")
    private String userBuDetailsId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CommonStatusEnum status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_unit_id")
    private BusinessUnit businessUnit;
}
