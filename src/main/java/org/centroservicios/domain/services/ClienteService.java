package org.centroservicios.domain.services;

import io.smallrye.mutiny.Uni;
import org.centroservicios.infrastructure.adapter.input.rest.common.ApiResponse;
import org.centroservicios.infrastructure.adapter.input.rest.dto.ClienteRequestDto;
import org.centroservicios.infrastructure.adapter.input.rest.dto.ClienteResponseDto;

import java.util.List;

public interface ClienteService {

    Uni<ApiResponse<ClienteResponseDto>> createCliente(ClienteRequestDto clienteRequestDto);

    Uni<ApiResponse<List<ClienteResponseDto>>> buscarClientePorNombresCompletos(String busqueda);

    Uni<ApiResponse<List<ClienteResponseDto>>> listarActivos(int page, int limit);

}
