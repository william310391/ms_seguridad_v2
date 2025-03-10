package com.ms_seguridad.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("permissions")
public class PermissionsEntity {
    @Id
    private Integer id;
    private String name;
}
