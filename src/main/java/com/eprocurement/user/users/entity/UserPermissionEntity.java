package com.eprocurement.user.users.entity;


import com.eprocurement.user.common.entity.BaseAuditEntity;
import com.eprocurement.user.common.enums.CommonStatusEnum;
import com.eprocurement.user.users.enums.FunctionalPermissionEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_permission",schema = "masters")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPermissionEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_permission_id")
    private String userPermissionId;

    @Column(name = "permission_type")
    @Enumerated(EnumType.STRING)
    private FunctionalPermissionEnum permissionType;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CommonStatusEnum status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
