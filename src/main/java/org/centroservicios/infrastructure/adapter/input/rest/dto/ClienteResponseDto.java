package org.centroservicios.infrastructure.adapter.input.rest.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record  ClienteResponseDto (
    UUID id,
    String nombres,
    String apellidos,
    String email,
    String telefono,
    Boolean activo
) {
    public ClienteResponseDto(UUID id, String nombres, String apellidos, String email,String telefono) {
        this(id, nombres, apellidos, email, telefono,null);
    }
}
