package org.centroservicios.infrastructure.adapter.input.rest.mapper;

import org.centroservicios.infrastructure.adapter.input.rest.dto.HorarioDisponibleRequestDto;
import org.centroservicios.infrastructure.adapter.output.entity.HorarioDisponibleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta-cdi")
public interface HorarioDisponibleMapper {

    HorarioDisponibleEntity toEntity(HorarioDisponibleRequestDto request);
}