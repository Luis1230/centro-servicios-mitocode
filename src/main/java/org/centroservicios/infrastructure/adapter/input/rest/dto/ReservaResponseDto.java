package org.centroservicios.infrastructure.adapter.input.rest.dto;

import lombok.Builder;
import org.centroservicios.domain.enums.ReservaType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Builder
public record ReservaResponseDto(
        UUID id,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        UUID clienteId,
        UUID profesionalId,
        ReservaType estado
) { }