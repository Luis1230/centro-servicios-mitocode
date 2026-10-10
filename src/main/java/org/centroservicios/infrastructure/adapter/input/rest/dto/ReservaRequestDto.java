package org.centroservicios.infrastructure.adapter.input.rest.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Schema(name = "ReservaRequestDto", description = "Información requerida para crear o actualizar un profesional")
public record ReservaRequestDto(

        @Schema(description = "Id del cliente", example = "3f2b8c1e-6a4d-4e59-9b7a-1c2d3e4f5a6b", required = true)
        @NotNull(message = "El clienteId es obligatorio")
        UUID clienteId,

        @Schema(description = "Id del profesional", example = "8a1d2c3b-4e5f-4a6b-8c7d-9e0f1a2b3c4d", required = true)
        @NotNull(message = "El profesionalId es obligatorio")
        UUID profesionalId,

        @Schema(description = "Fecha de la reserva", example = "2026-10-05", required = true)
        @NotNull(message = "La fecha es obligatoria")
        @FutureOrPresent(message = "La fecha no puede ser pasada")
        LocalDate fecha,

        @Schema(description = "Hora de inicio", example = "09:00:00", type = SchemaType.STRING, format = "time", required = true)
        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime horaInicio,

        @Schema(description = "Hora de fin", example = "10:00:00", type = SchemaType.STRING, format = "time", required = true)
        @NotNull(message = "La hora de fin es obligatoria")
        LocalTime horaFin
) { }