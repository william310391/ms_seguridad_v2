package com.ms_seguridad.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("user_roles")
public class UserRolesEntity {
  @Id
  private Integer id;
  @Column("role_id")
  private Integer idRole;
  @Column("user_id")
  private Integer idUser;
}