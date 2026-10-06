package org.centroservicios.infrastructure.adapter.input.rest.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record HorarioDisponibleResponseDto(
        UUID id,
        String mensaje
) { }
