package org.centroservicios.infrastructure.adapter.output.repository;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import org.centroservicios.infrastructure.adapter.input.rest.common.PageResponse;
import org.centroservicios.infrastructure.adapter.output.entity.ClienteEntity;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ClienteRepository implements PanacheRepositoryBase<ClienteEntity, UUID> {

    /**
     * Buscar clientes por nombres y/o apellidos.
     *
     * Ejemplos:
     * Luis       -> Luis Alejandro Muñante Escate
     * Muñante    -> Luis Alejandro Muñante Escate
     * Escate     -> Luis Alejandro Muñante Escate
     * Luis Muñante -> Luis Alejandro Muñante Escate
     */
    public List<ClienteEntity> buscarPorNombresCompletos(String busqueda) {

        String filtro = "%" + busqueda.trim().toLowerCase() + "%";

        return find("""
                LOWER(CONCAT(nombres, ' ', apellidos)) LIKE ?1
                """, filtro)
                .list();
    }

    /**
     * Listar clientes activos.
     */
    public PageResponse<ClienteEntity> listarActivos(int page, int limit) {

        PanacheQuery<ClienteEntity> query = find("estadoActivo = true", Sort.by("apellidos"))
                .page(Page.of(page, limit));

        query.page(Page.of(page - 1, limit ));

        long totalElements = query.count();
        int totalPages = query.pageCount();

        return new PageResponse<ClienteEntity>(query.list(), page, limit, totalElements, totalPages);
    }

}
