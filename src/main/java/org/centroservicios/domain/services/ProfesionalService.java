package org.centroservicios.domain.services;

import org.centroservicios.infrastructure.adapter.input.rest.common.ApiResponse;
import org.centroservicios.infrastructure.adapter.input.rest.dto.ProfesionalRequestDto;
import org.centroservicios.infrastructure.adapter.input.rest.dto.ProfesionalResponseDto;

import java.util.List;

public interface ProfesionalService {

    ApiResponse<ProfesionalResponseDto> createProfesional(ProfesionalRequestDto profesionalRequestDto);

    ApiResponse<List<ProfesionalResponseDto>> buscarProfesionalPorNombresCompletos(String busqueda);

    ApiResponse<List<ProfesionalResponseDto>> listarActivos();

    ApiResponse<List<ProfesionalResponseDto>> buscarPorEspecialidad(String especialidad);


}
