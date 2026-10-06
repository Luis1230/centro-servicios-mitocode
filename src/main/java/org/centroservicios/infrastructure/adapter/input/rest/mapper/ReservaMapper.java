package org.centroservicios.infrastructure.adapter.input.rest.mapper;

import org.centroservicios.infrastructure.adapter.input.rest.dto.ReservaRequestDto;
import org.centroservicios.infrastructure.adapter.input.rest.dto.ReservaResponseDto;
import org.centroservicios.infrastructure.adapter.output.entity.ReservaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "jakarta-cdi")
public interface ReservaMapper {

    @Mapping(target = "id", ignore = true)
    ReservaEntity toEntity(ReservaRequestDto request);

    ReservaResponseDto toResponse(ReservaEntity entity);

    List<ReservaResponseDto> toResponseList(List<ReservaEntity> entities);
}
