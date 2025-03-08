package com.ms_seguridad.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("role_permissions")
public record RolePermissionsEntity(
  @Id Integer id,
  @Column("role_id") Integer idRole,
  @Column("permission_id") Integer idPermission
) {}
