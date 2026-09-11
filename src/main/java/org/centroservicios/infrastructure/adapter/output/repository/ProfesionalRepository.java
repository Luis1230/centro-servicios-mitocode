package org.centroservicios.infrastructure.adapter.output.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.centroservicios.infrastructure.adapter.output.entity.ProfesionalEntity;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ProfesionalRepository implements PanacheRepositoryBase<ProfesionalEntity, UUID> {

    /**
     * Buscar profesionales por nombres y/o apellidos.
     *
     * Ejemplos:
     * Luis       -> Luis Alejandro Muñante Escate
     * Muñante    -> Luis Alejandro Muñante Escate
     * Escate     -> Luis Alejandro Muñante Escate
     * Luis Muñante -> Luis Alejandro Muñante Escate
     */
    public List<ProfesionalEntity> buscarPorNombresCompletos(String busqueda) {

        String filtro = "%" + busqueda.trim().toLowerCase() + "%";

        return find("""
                LOWER(CONCAT(nombres, ' ', apellidos)) LIKE ?1
                """, filtro)
                .list();
    }

    /**
     * Listar profesionales activos.
     */
    public List<ProfesionalEntity> listarActivos() {

        return find("""
                estadoActivo = true
                """)
                .list();
    }

    /**
     * Buscar profesionales por especialidad.
     *
     * Ejemplo:
     * "psicología," -> profesionales de psicología,
     */
    public List<ProfesionalEntity> buscarPorEspecialidad(String especialidad) {

        String filtro = "%" + especialidad.trim().toLowerCase() + "%";

        return find("""
                LOWER(especialidad) LIKE ?1
                """, filtro)
                .list();
    }

}