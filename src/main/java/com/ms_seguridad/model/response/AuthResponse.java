package com.ms_seguridad.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"username", "message", "jwt", "status" })
public class AuthResponse {
   private String username;
   private  String message;
   private String jwt;
   private  boolean status;
}
