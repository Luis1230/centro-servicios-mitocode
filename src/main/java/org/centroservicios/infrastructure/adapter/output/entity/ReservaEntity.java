package org.centroservicios.infrastructure.adapter.output.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.centroservicios.domain.enums.ReservaType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "reserva", schema = "centroservicios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, columnDefinition = "UNIQUEIDENTIFIER")
    private UUID id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    @Column(name = "profesional_id", nullable = false)
    private UUID profesionalId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservaType estado;
}