package org.centroservicios.domain.services;

import io.smallrye.mutiny.Uni;
import org.centroservicios.infrastructure.adapter.input.rest.common.ApiResponse;
import org.centroservicios.infrastructure.adapter.input.rest.dto.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ReservaService {

    Uni<ApiResponse<ReservaResponseDto>> createReserva(ReservaRequestDto request);

    Uni<ApiResponse<ReservaResponseDto>> updateReserva(UUID id, ReservaUpdateRequestDto request);

    Uni<ApiResponse<ReservaResponseDto>> cancelarReserva(UUID id);

    Uni<ApiResponse<List<ProfesionalReservasDto>>> profesionalesPorReservasActivas();
    Uni<ApiResponse<Map<LocalDate, List<ReservaDetalleDto>>>> reservasPorFecha();

}
