package com.ms_seguridad.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public record UsersEntity(
    @Id Integer id, 
    String username,
    String password,
    @Column("is_enabled") Boolean isEnabled,
    @Column("account_no_expired") Boolean isAccountNoExpired,
    @Column("account_no_Locked") Boolean isAccountNoLocked,
    @Column("creadential_No_Expired") Boolean isCredentialNoExpired
) {}
