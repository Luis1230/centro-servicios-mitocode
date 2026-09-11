package org.centroservicios.infrastructure.adapter.input.rest.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ProfesionalResponseDto(
        UUID id,
    String nombres,
    String apellidos,
    String especialidad,
    Boolean activo
) {
    public ProfesionalResponseDto(UUID id, String nombres, String apellidos, String especialidad) {
        this(id, nombres, apellidos, especialidad, null);
    }
}
