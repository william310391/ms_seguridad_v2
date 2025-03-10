package com.ms_seguridad.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("users")
public class UsersEntity {
    @Id
    private Integer id;
    private String username;
    private String password;
    @Column("is_enabled")
    private Boolean isEnabled;
    @Column("account_no_expired")
    private Boolean isAccountNoExpired;
    @Column("account_no_Locked")
    private Boolean isAccountNoLocked;
    @Column("creadential_No_Expired")
    private Boolean isCredentialNoExpired;
}
