package com.ms_seguridad.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("roles")
public record RolesEntity(
    @Id Integer id, 
    String role_name 
) {}
