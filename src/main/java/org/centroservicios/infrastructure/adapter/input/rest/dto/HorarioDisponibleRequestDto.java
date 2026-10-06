package org.centroservicios.infrastructure.adapter.input.rest.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record HorarioDisponibleRequestDto(

        @Schema(description = "Id del profesional",
                example = "3f2b8c1e-6a4d-4e59-9b7a-1c2d3e4f5a6b",
                required = true)
        @NotNull(message = "El profesionalId es obligatorio")
        UUID profesionalId,

        @Schema(description = "Fecha del horario",
                example = "2026-10-05",
                required = true)
        @NotNull(message = "La fecha es obligatoria")
        @FutureOrPresent(message = "La fecha no puede ser pasada")
        LocalDate fecha,

        @Schema(description = "Hora de inicio",
                example = "09:00:00", type = SchemaType.STRING, format = "time",
                required = true)
        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime horaInicio,

        @Schema(description = "Hora de fin",
                example = "12:00:00", type = SchemaType.STRING, format = "time",
                required = true)
        @NotNull(message = "La hora de fin es obligatoria")
        LocalTime horaFin
) { }