package org.centroservicios.application;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.centroservicios.domain.enums.ErrorType;
import org.centroservicios.domain.exception.BusinessException;
import org.centroservicios.domain.services.ClienteService;
import org.centroservicios.infrastructure.adapter.input.rest.common.ApiResponse;
import org.centroservicios.infrastructure.adapter.input.rest.dto.ClienteRequestDto;
import org.centroservicios.infrastructure.adapter.input.rest.dto.ClienteResponseDto;
import org.centroservicios.infrastructure.adapter.input.rest.mapper.ClienteMapper;
import org.centroservicios.infrastructure.adapter.output.entity.ClienteEntity;
import org.centroservicios.infrastructure.adapter.output.repository.ClienteRepository;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import java.time.Instant;
import java.util.List;

/**
 * Caso de uso encargado de gestionar las operaciones relacionadas
 * con los clientes.
 *
 * <p>Esta clase implementa {@link ClienteService} y actúa como
 * capa de aplicación entre los adaptadores de entrada y el repositorio
 * de clientes.</p>
 *
 * <p>Las operaciones principales son:</p>
 * <ul>
 *     <li>Crear un cliente.</li>
 *     <li>Buscar clientes por nombres y apellidos.</li>
 *     <li>Listar clientes activos.</li>
 * </ul>
 */
@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ClienteUseCase  implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;


    @Fallback(fallbackMethod = "fallbackCreateClient")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 10000)
    @Retry(maxRetries = 2, delay = 500)
    @Timeout(2000)
    @WithTransaction
    @Override
    public Uni<ApiResponse<ClienteResponseDto>> createCliente(ClienteRequestDto clienteRequestDto) {

        ClienteEntity clienteEntity = clienteMapper.toEntity(clienteRequestDto);
        clienteEntity.setEstadoActivo(true);

        return clienteRepository.persist(clienteEntity)
                .map(saved -> ApiResponse.<ClienteResponseDto>builder()
                        .data(ClienteResponseDto.builder()
                                .id(clienteEntity.getId())
                                .nombres(clienteEntity.getNombres())
                                .apellidos(clienteEntity.getApellidos())
                                .email(clienteEntity.getEmail())
                                .telefono(clienteEntity.getTelefono())
                                .activo(clienteEntity.isEstadoActivo())
                                .build())
                        .statusCode(HttpResponseStatus.CREATED.code())
                        .message("Cliente creado exitosamente")
                        .timestamp(Instant.now())
                        .build());
    }

    public Uni<ApiResponse<ClienteResponseDto>> fallbackCreateClient(ClienteRequestDto clienteRequestDto) {
        throw new BusinessException(
                ErrorType.CIRCUIT_BREAKER_ERROR,
                ErrorType.CIRCUIT_BREAKER_ERROR.getDescription());
    }

    @WithSession
    @Override
    public Uni<ApiResponse<List<ClienteResponseDto>>> buscarClientePorNombresCompletos(String busqueda) {

        return clienteRepository.buscarPorNombresCompletos(busqueda)
                .map(clienteLst -> ApiResponse.<List<ClienteResponseDto>>builder()
                        .data(clienteMapper.toResponseList(clienteLst))
                        .statusCode(HttpResponseStatus.OK.code())
                        .message("Se obtuvo correctamente la información requerida.")
                        .totalElements(clienteLst.size())
                        .timestamp(Instant.now())
                        .build());
    }

    @WithSession
    @Override
    public Uni<ApiResponse<List<ClienteResponseDto>>> listarActivos(int page, int limit) {

        return clienteRepository.listarActivos(page, limit)
                .map(pageResponse -> ApiResponse.<List<ClienteResponseDto>>builder()
                        .data(clienteMapper.toResponseList(pageResponse.data()))
                        .statusCode(HttpResponseStatus.OK.code())
                        .message("Se obtuvo correctamente la información requerida.")
                        .currentPage(pageResponse.currentPage())
                        .totalElements((int) pageResponse.totalElements())
                        .totalPages(pageResponse.totalPAges())
                        .timestamp(Instant.now())
                        .build());
    }
}
