package org.centroservicios.infrastructure.adapter.input.rest.mapper;

import org.centroservicios.infrastructure.adapter.input.rest.dto.ClienteRequestDto;
import org.centroservicios.infrastructure.adapter.input.rest.dto.ClienteResponseDto;
import org.centroservicios.infrastructure.adapter.output.entity.ClienteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper encargado de convertir objetos relacionados
 * con el cliente.
 *
 * <p>Utiliza MapStruct para realizar el mapeo entre los DTO
 * utilizados en la capa de entrada y las entidades utilizadas
 * para la persistencia.</p>
 */
@Mapper(componentModel = "cdi")
public interface ClienteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estadoActivo", ignore = true)
    ClienteEntity toEntity(ClienteRequestDto request);

    @Mapping(source = "estadoActivo", target = "activo")
    ClienteResponseDto toDto(ClienteEntity entity);

    List<ClienteResponseDto> toResponseList(List<ClienteEntity> entities);

}
