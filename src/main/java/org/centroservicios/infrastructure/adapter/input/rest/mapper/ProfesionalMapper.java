package org.centroservicios.infrastructure.adapter.input.rest.mapper;

import org.centroservicios.infrastructure.adapter.input.rest.dto.ProfesionalRequestDto;
import org.centroservicios.infrastructure.adapter.output.entity.ProfesionalEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
/**
 * Mapper encargado de convertir objetos relacionados
 * con el profesional.
 *
 * <p>Utiliza MapStruct para realizar el mapeo entre los DTO
 * utilizados en la capa de entrada y las entidades utilizadas
 * para la persistencia.</p>
 */
@Mapper(componentModel = "cdi")
public interface ProfesionalMapper {

    /**
     * Convierte un {@link ProfesionalRequestDto} en un
     * {@link ProfesionalEntity}.
     *
     * <p>El identificador no se mapea desde el request debido a que
     * es generado automáticamente por Hibernate/JPA.</p>
     *
     * @param request información del profesional recibida desde
     *                la capa de entrada
     * @return entidad de profesional lista para ser persistida
     */
    @Mapping(target = "id", ignore = true)
    ProfesionalEntity toEntity(ProfesionalRequestDto request);
}
