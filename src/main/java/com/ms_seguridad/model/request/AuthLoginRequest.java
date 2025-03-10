package com.ms_seguridad.model.request;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequest(@NotBlank String username
                              ,@NotBlank String password) {

    
}