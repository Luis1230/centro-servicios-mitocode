package org.centroservicios.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
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

    @Transactional
    @Override
    public ApiResponse<ProfesionalResponseDto> createProfesional(ProfesionalRequestDto profesionalRequestDto) {

        ProfesionalEntity profesionalEntity = profesionalMapper.toEntity(profesionalRequestDto);
        profesionalRepository.persist(profesionalEntity);

        return ApiResponse.<ProfesionalResponseDto>builder()
                .data(ProfesionalResponseDto.builder()
                        .id(profesionalEntity.getId())
                        .nombres(profesionalEntity.getNombres())
                        .apellidos(profesionalEntity.getApellidos())
                        .especialidad(profesionalEntity.getEspecialidad())
                        .activo(profesionalEntity.isEstadoActivo())
                        .build())
                .statusCode(201)
                .message("Profesional creado exitosamente")
                .timestamp(Instant.now())
                .build();
    }

    @Override
    public ApiResponse<List<ProfesionalResponseDto>> buscarProfesionalPorNombresCompletos(String busqueda) {
        return null;
    }

    @Override
    public ApiResponse<List<ProfesionalResponseDto>> listarActivos() {
        return null;
    }

    @Override
    public ApiResponse<List<ProfesionalResponseDto>> buscarPorEspecialidad(String especialidad) {
        return null;
    }
}
