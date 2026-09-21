package org.centroservicios.infrastructure.adapter.output.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "profesional")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfesionalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(name = "nombres", nullable = false)
    public String nombres;

    @Column(name = "apellidos", nullable = false)
    public String apellidos;

    @Column(name = "especialidad", nullable = false)
    public String especialidad;

    @Column(name = "estado_activo", nullable = false)
    public boolean estadoActivo = true;
}
