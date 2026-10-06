package org.centroservicios.application;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.centroservicios.domain.enums.ErrorType;
import org.centroservicios.domain.exception.BusinessException;
import org.centroservicios.domain.services.HorarioDisponibleService;
import org.centroservicios.infrastructure.adapter.input.rest.common.ApiResponse;
import org.centroservicios.infrastructure.adapter.input.rest.dto.HorarioDisponibleRequestDto;
import org.centroservicios.infrastructure.adapter.input.rest.dto.HorarioDisponibleResponseDto;
import org.centroservicios.infrastructure.adapter.input.rest.mapper.HorarioDisponibleMapper;
import org.centroservicios.infrastructure.adapter.output.entity.HorarioDisponibleEntity;
import org.centroservicios.infrastructure.adapter.output.repository.HorarioDisponibleRepository;
import org.centroservicios.infrastructure.adapter.output.repository.ProfesionalRepository;

import java.time.Instant;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class HorarioDisponibleUseCase implements HorarioDisponibleService {

    private final HorarioDisponibleRepository horarioDisponibleRepository;
    private final ProfesionalRepository profesionalRepository;
    private final HorarioDisponibleMapper horarioDisponibleMapper;

    @WithTransaction
    @Override
    public Uni<ApiResponse<HorarioDisponibleResponseDto>> createHorarioDisponible(HorarioDisponibleRequestDto request) {

        if (!request.horaInicio().isBefore(request.horaFin())) {
            return Uni.createFrom().failure(new BusinessException(
                    ErrorType.HORARIO_INVALIDO,
                    ErrorType.HORARIO_INVALIDO.getDescription()));
        }

        return profesionalRepository.findById(request.profesionalId())
                .onItem().ifNull().failWith(() ->
                        new BusinessException(
                                ErrorType.PROFESIONAL_NO_EXISTE,
                                ErrorType.PROFESIONAL_NO_EXISTE.getDescription()))
                .invoke(profesional -> {
                    if (!profesional.isEstadoActivo()) {
                        throw new BusinessException(
                                ErrorType.PROFESIONAL_DESACTIVADO,
                                ErrorType.PROFESIONAL_DESACTIVADO.getDescription());
                    }
                })
                .chain(() -> horarioDisponibleRepository.existeSolapamientoCreate(
                        request.profesionalId(),
                        request.fecha(),
                        request.horaInicio(),
                        request.horaFin()))
                .invoke(existeSolapamiento -> {
                    if (existeSolapamiento){
                    throw new BusinessException(
                            ErrorType.HORARIO_SOLAPADO,
                            ErrorType.HORARIO_SOLAPADO.getDescription());
                    }
                })
                .chain( () -> {
                    HorarioDisponibleEntity entity = horarioDisponibleMapper.toEntity(request);
                    entity.setEstado(true);
                    return horarioDisponibleRepository.persist(entity);
                })
                .map(saved -> ApiResponse.<HorarioDisponibleResponseDto>builder()
                        .data(HorarioDisponibleResponseDto.builder()
                                .id(saved.getId())
                                .mensaje("Horario creado exitosamente.")
                                .build())
                        .statusCode(HttpResponseStatus.CREATED.code())
                        .message("Horario creado exitosamente.")
                        .timestamp(Instant.now())
                        .build());
    }
}