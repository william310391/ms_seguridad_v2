package com.ms_seguridad.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("role_permissions")
public class RolePermissionsEntity {
  @Id
  private Integer id;
  @Column("role_id")
  private Integer idRole;
  @Column("permission_id")
  private Integer idPermission;
}
