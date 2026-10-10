package org.centroservicios.infrastructure.adapter.output.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.centroservicios.infrastructure.adapter.output.entity.HorarioDisponibleEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@ApplicationScoped
public class HorarioDisponibleRepository implements PanacheRepositoryBase<HorarioDisponibleEntity, UUID> {

    public Uni<Boolean> existeSolapamientoCreate(UUID profesionalId, LocalDate fecha,
                                           LocalTime inicio, LocalTime fin) {
        return count("""
                profesionalId = ?1
                and fecha = ?2
                and estado = true
                and horaInicio < ?4
                and horaFin > ?3
                """, profesionalId, fecha, inicio, fin)
                .map(total -> total > 0);
    }

    public Uni<Boolean> existeHorarioProfesional(UUID profesionalId, LocalDate fecha,
                                              LocalTime inicio, LocalTime fin) {
        return count("""
            profesionalId = ?1 and fecha = ?2 and estado = true
            and horaInicio <= ?3 and horaFin >= ?4
            """, profesionalId, fecha, inicio, fin)
                .map(total -> total > 0);
    }

}
