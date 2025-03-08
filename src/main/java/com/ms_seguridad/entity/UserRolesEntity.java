package com.ms_seguridad.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("user_roles")
public record UserRolesEntity(
  @Id Integer id,
  @Column("role_id") Integer idRole,
  @Column("user_id") Integer idUser
) {}