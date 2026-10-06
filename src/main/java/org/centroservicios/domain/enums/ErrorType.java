package org.centroservicios.domain.enums;

import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum ErrorType {

    VALIDATION_ERROR("V-01", "Error en la validación de datos", Response.Status.BAD_REQUEST),
    GENERIC_ERROR("G-02", "Error interno del sistema. Reintentar más tarde", Response.Status.INTERNAL_SERVER_ERROR),
    PROFESIONAL_NO_EXISTE("A001", "El profesional no existe.", Response.Status.PRECONDITION_REQUIRED),
    PROFESIONAL_DESACTIVADO("A002", "El profesional se encuentra desactivado", Response.Status.PRECONDITION_REQUIRED),
    HORARIO_SOLAPADO("A003","El profesional ya tiene un horario que se cruza con el intervalo solicitado", Response.Status.PRECONDITION_REQUIRED),
    HORARIO_INVALIDO("A004","La hora de inicio debe ser menor que la hora de fin", Response.Status.PRECONDITION_REQUIRED),
    CLIENTE_NO_EXISTE("A005","El cliente no existe", Response.Status.PRECONDITION_REQUIRED),
    CLIENTE_DESACTIVADO("A006","El cliente está desactivado", Response.Status.PRECONDITION_REQUIRED),
    HORARIO_NO_DISPONIBLE("A007","El profesional no tiene un horario disponible que cubra el intervalo solicitado", Response.Status.PRECONDITION_REQUIRED),
    RESERVA_SOLAPADA("A008","El profesional ya tiene una reserva activa que se cruza con el intervalo solicitado", Response.Status.PRECONDITION_REQUIRED),
    RESERVA_NO_EXISTE("A009","La reserva no existe", Response.Status.PRECONDITION_REQUIRED),
    RESERVA_NO_MODIFICABLE("A010","Solo se pueden modificar reservas en estado CREADA", Response.Status.PRECONDITION_REQUIRED),
    RESERVA_NO_CANCELABLE("A011","Solo se pueden cancelar reservas en estado CREADA", Response.Status.PRECONDITION_REQUIRED);

    private final String code;
    private final String description;
    private final Response.Status status;
}
