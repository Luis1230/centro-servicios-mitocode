package org.centroservicios.application;

import io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.centroservicios.domain.services.ProfesionalService;
import org.centroservicios.infrastructure.adapter.input.rest.common.ApiResponse;
import org.centroservicios.infrastructure.adapter.input.rest.common.PageResponse;
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

    @Transactional
    @Override
    public ApiResponse<ProfesionalResponseDto> createProfesional(ProfesionalRequestDto profesionalRequestDto) {

        ProfesionalEntity profesionalEntity = profesionalMapper.toEntity(profesionalRequestDto);
        profesionalEntity.setEstadoActivo(true);
        profesionalRepository.persist(profesionalEntity);

        return ApiResponse.<ProfesionalResponseDto>builder()
                .data(profesionalMapper.toResponse(profesionalEntity))
                .statusCode(HttpResponseStatus.CREATED.code())
                .message("Profesional creado exitosamente")
                .timestamp(Instant.now())
                .build();
    }

    @Override
    public ApiResponse<List<ProfesionalResponseDto>> buscarProfesionalPorNombresCompletos(String busqueda) {

        List<ProfesionalEntity> profesionalLst = profesionalRepository.buscarPorNombresCompletos(busqueda);

        return ApiResponse.<List<ProfesionalResponseDto>>builder()
                .data(profesionalMapper.toResponseList(profesionalLst))
                .statusCode(HttpResponseStatus.OK.code())
                .message("Se obtuvo correctamente la información requerida.")
                .totalElements(profesionalLst.size())
                .timestamp(Instant.now())
                .build();
    }

    @Override
    public ApiResponse<List<ProfesionalResponseDto>> listarActivos(int page, int limit) {

        PageResponse<ProfesionalEntity> pageResponse = profesionalRepository.listarActivos(page, limit);

        return ApiResponse.<List<ProfesionalResponseDto>>builder()
                .data(profesionalMapper.toResponseList(pageResponse.data()))
                .statusCode(HttpResponseStatus.OK.code())
                .message("Se obtuvo correctamente la información requerida.")
                .currentPage(pageResponse.currentPage())
                .totalElements((int)pageResponse.totalElements())
                .totalPages(pageResponse.totalPAges())
                .timestamp(Instant.now())
                .build();
    }
}
