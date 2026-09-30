package org.centroservicios.domain.services;

import org.centroservicios.infrastructure.adapter.input.rest.common.ApiResponse;
import org.centroservicios.infrastructure.adapter.input.rest.dto.ClienteRequestDto;
import org.centroservicios.infrastructure.adapter.input.rest.dto.ClienteResponseDto;

import java.util.List;

public interface ClienteService {

    ApiResponse<ClienteResponseDto> createCliente(ClienteRequestDto clienteRequestDto);

    ApiResponse<List<ClienteResponseDto>> buscarClientePorNombresCompletos(String busqueda);

    ApiResponse<List<ClienteResponseDto>> listarActivos(int page, int limit);

}
