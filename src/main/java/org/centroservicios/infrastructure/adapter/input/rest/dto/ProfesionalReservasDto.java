package org.centroservicios.infrastructure.adapter.input.rest.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ProfesionalReservasDto(
        UUID id,
        String nombres,
        String apellidos,
        String especialidad,
        long reservasActivas
) { }
