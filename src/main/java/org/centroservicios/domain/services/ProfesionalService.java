package org.centroservicios.domain.services;

import io.smallrye.mutiny.Uni;
import org.centroservicios.infrastructure.adapter.input.rest.common.ApiResponse;
import org.centroservicios.infrastructure.adapter.input.rest.dto.ProfesionalRequestDto;
import org.centroservicios.infrastructure.adapter.input.rest.dto.ProfesionalResponseDto;

import java.util.List;

public interface ProfesionalService {

    Uni<ApiResponse<ProfesionalResponseDto>> createProfesional(ProfesionalRequestDto profesionalRequestDto);

    Uni<ApiResponse<List<ProfesionalResponseDto>>> buscarProfesionalPorNombresCompletos(String busqueda);

    Uni<ApiResponse<List<ProfesionalResponseDto>>> listarActivos(int page, int limit);

}
