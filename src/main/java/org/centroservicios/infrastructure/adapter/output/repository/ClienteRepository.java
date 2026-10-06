package org.centroservicios.infrastructure.adapter.output.repository;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
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
    public Uni<List<ClienteEntity>> buscarPorNombresCompletos(String busqueda) {

        String filtro = "%" + busqueda.trim().toLowerCase() + "%";

        return find("""
                LOWER(CONCAT(nombres, ' ', apellidos)) LIKE ?1
                """, filtro)
                .list();
    }

    /**
     * Listar clientes activos.
     */
    public Uni<PageResponse<ClienteEntity>> listarActivos(int page, int limit) {

        PanacheQuery<ClienteEntity> query = find("estadoActivo = true", Sort.by("apellidos"))
                .page(Page.of(page, limit));


        return query.list()
                .chain(lista -> query.count()
                        .map(total -> {
                            int totalPages = (int) Math.ceil((double) total / limit);
                            return new PageResponse<>(lista, page, limit, total, totalPages);
                        }));}

}
