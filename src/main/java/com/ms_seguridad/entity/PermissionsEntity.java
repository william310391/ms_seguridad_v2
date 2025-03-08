package com.ms_seguridad.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("permissions")
public record PermissionsEntity(
    @Id Integer id, 
    String name 
) {}
