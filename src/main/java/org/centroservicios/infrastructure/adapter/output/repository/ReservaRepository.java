package org.centroservicios.infrastructure.adapter.output.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.centroservicios.domain.enums.ReservaType;
import org.centroservicios.infrastructure.adapter.output.entity.ReservaEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ReservaRepository implements PanacheRepositoryBase<ReservaEntity, UUID> {

    public Uni<Boolean> existeSolapamientoCreate(UUID profesionalId, LocalDate fecha,
                                           LocalTime inicio, LocalTime fin) {

        Uni<Long> total = count("""
                        profesionalId = ?1 and fecha = ?2 and estado = ?3
                        and horaInicio < ?5 and horaFin > ?4
                        """, profesionalId, fecha, ReservaType.CREADA, inicio, fin);

        return total.map(t -> t > 0);
    }

    public Uni<Boolean> existeSolapamientoUpdate(UUID profesionalId, LocalDate fecha,
                                                 LocalTime inicio, LocalTime fin, UUID excluirId) {
        Uni<Long> total = count("""
                        profesionalId = ?1 and fecha = ?2 and estado = ?3
                        and horaInicio < ?5 and horaFin > ?4 and id <> ?6
                        """, profesionalId, fecha, ReservaType.CREADA, inicio, fin, excluirId);

        return total.map(t -> t > 0);
    }

    public Uni<List<ReservaEntity>> listarActivas() {
        return list("estado", ReservaType.CREADA);
    }
}