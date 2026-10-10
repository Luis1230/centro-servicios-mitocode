package org.centroservicios.application;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.centroservicios.domain.services.ProfesionalService;
import org.centroservicios.infrastructure.adapter.input.rest.common.ApiResponse;
import org.centroservicios.infrastructure.adapter.input.rest.dto.ProfesionalRequestDto;
import org.centroservicios.infrastructure.adapter.input.rest.dto.ProfesionalResponseDto;
import org.centroservicios.infrastructure.adapter.input.rest.mapper.ProfesionalMapper;
import org.centroservicios.infrastructure.adapter.output.entity.ProfesionalEntity;
import org.centroservicios.infrastructure.adapter.output.repository.ProfesionalRepository;

import java.time.Instant;
import java.util.List;

/**
 * Caso de uso encargado de gestionar las operaciones relacionadas
 * con los profesionales.
 *
 * <p>Esta clase implementa {@link ProfesionalService} y actúa como
 * capa de aplicación entre los adaptadores de entrada y el repositorio
 * de profesionales.</p>
 *
 * <p>Las operaciones principales son:</p>
 * <ul>
 *     <li>Crear un profesional.</li>
 *     <li>Buscar profesionales por nombres y apellidos.</li>
 *     <li>Listar profesionales activos.</li>
 *     <li>Buscar profesionales por especialidad.</li>
 * </ul>
 */
@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ProfesionalUseCase implements ProfesionalService {

    private final ProfesionalRepository profesionalRepository;
    private final ProfesionalMapper profesionalMapper;

    @WithTransaction
    @Override
    public Uni<ApiResponse<ProfesionalResponseDto>> createProfesional(ProfesionalRequestDto profesionalRequestDto) {

        ProfesionalEntity profesionalEntity = profesionalMapper.toEntity(profesionalRequestDto);
        profesionalEntity.setEstadoActivo(true);

        return profesionalRepository.persist(profesionalEntity)
                .map(saved -> ApiResponse.<ProfesionalResponseDto>builder()
                        .data(profesionalMapper.toResponse(saved))
                        .statusCode(HttpResponseStatus.CREATED.code())
                        .message("Profesional creado exitosamente")
                        .timestamp(Instant.now())
                        .build());
    }

    @WithSession
    @Override
    public Uni<ApiResponse<List<ProfesionalResponseDto>>> buscarProfesionalPorNombresCompletos(String busqueda) {

        return profesionalRepository.buscarPorNombresCompletos(busqueda)
                .map(profesionalLst -> ApiResponse.<List<ProfesionalResponseDto>>builder()
                        .data(profesionalMapper.toResponseList(profesionalLst))
                        .statusCode(HttpResponseStatus.OK.code())
                        .message("Se obtuvo correctamente la información requerida.")
                        .totalElements(profesionalLst.size())
                        .timestamp(Instant.now())
                        .build());
    }

    @WithSession
    @Override
    public Uni<ApiResponse<List<ProfesionalResponseDto>>> listarActivos(int page, int limit) {

        return profesionalRepository.listarActivos(page, limit)
                .map(pageResponse -> ApiResponse.<List<ProfesionalResponseDto>>builder()
                        .data(profesionalMapper.toResponseList(pageResponse.data()))
                        .statusCode(HttpResponseStatus.OK.code())
                        .message("Se obtuvo correctamente la información requerida.")
                        .currentPage(pageResponse.currentPage())
                        .totalElements((int) pageResponse.totalElements())
                        .totalPages(pageResponse.totalPAges())
                        .timestamp(Instant.now())
                        .build());
    }
}
