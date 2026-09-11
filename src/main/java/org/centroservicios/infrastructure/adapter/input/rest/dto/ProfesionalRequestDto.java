package org.centroservicios.infrastructure.adapter.input.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "ProfesionalRequestDto", description = "Información requerida para crear o actualizar un profesional")
public record ProfesionalRequestDto(

        @Schema(description = "Nombres del profesional", example = "Ana Maria", required = true)
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombres,

        @Schema(description = "Apellidos del profesional", example = "Torres Diaz", required = true)
        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
        String apellidos,

        @Schema(description = "Especialidad del profesional", example = "Cardiologia", required = true)
        @NotBlank(message = "La especialidad es obligatoria")
        @Size(max = 100, message = "La especialidad no puede superar los 100 caracteres")
        String especialidad
) { }