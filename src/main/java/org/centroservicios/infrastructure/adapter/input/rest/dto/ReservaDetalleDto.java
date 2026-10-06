package org.centroservicios.infrastructure.adapter.input.rest.dto;

import lombok.Builder;

import java.time.LocalTime;
import java.util.UUID;

@Builder
public record ReservaDetalleDto(
        UUID id,
        LocalTime horaInicio,
        LocalTime horaFin,
        String cliente,
        String profesional
) { }
