package com.ms_seguridad.model.request;

import jakarta.validation.constraints.NotBlank;

public record AuthCreateUserRequest(@NotBlank String userName,@NotBlank String password,AuthCreateRoleRequest roleRequest) {    
}
