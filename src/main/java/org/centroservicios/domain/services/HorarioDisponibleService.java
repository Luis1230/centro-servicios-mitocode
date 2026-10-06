package org.centroservicios.domain.services;

import io.smallrye.mutiny.Uni;
import org.centroservicios.infrastructure.adapter.input.rest.common.ApiResponse;
import org.centroservicios.infrastructure.adapter.input.rest.dto.HorarioDisponibleRequestDto;
import org.centroservicios.infrastructure.adapter.input.rest.dto.HorarioDisponibleResponseDto;

public interface HorarioDisponibleService {

    Uni<ApiResponse<HorarioDisponibleResponseDto>> createHorarioDisponible(HorarioDisponibleRequestDto request);


}
