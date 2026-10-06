package org.centroservicios.application;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.centroservicios.domain.enums.ErrorType;
import org.centroservicios.domain.enums.ReservaType;
import org.centroservicios.domain.exception.BusinessException;
import org.centroservicios.domain.services.ReservaService;
import org.centroservicios.infrastructure.adapter.input.rest.common.ApiResponse;
import org.centroservicios.infrastructure.adapter.input.rest.dto.*;
import org.centroservicios.infrastructure.adapter.input.rest.mapper.ReservaMapper;
import org.centroservicios.infrastructure.adapter.output.entity.ClienteEntity;
import org.centroservicios.infrastructure.adapter.output.entity.ProfesionalEntity;
import org.centroservicios.infrastructure.adapter.output.entity.ReservaEntity;
import org.centroservicios.infrastructure.adapter.output.repository.ClienteRepository;
import org.centroservicios.infrastructure.adapter.output.repository.HorarioDisponibleRepository;
import org.centroservicios.infrastructure.adapter.output.repository.ProfesionalRepository;
import org.centroservicios.infrastructure.adapter.output.repository.ReservaRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ReservaUseCase implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final ProfesionalRepository profesionalRepository;
    private final HorarioDisponibleRepository horarioDisponibleRepository;
    private final ReservaMapper reservaMapper;
    private final ClienteRepository clienteRepository;

    @WithTransaction
    @Override
    public Uni<ApiResponse<ReservaResponseDto>> createReserva(ReservaRequestDto request) {

        if (!request.horaInicio().isBefore(request.horaFin())) {
            throw new BusinessException(
                    ErrorType.HORARIO_INVALIDO,
                    ErrorType.HORARIO_INVALIDO.getDescription());
        }

        return clienteRepository.findById(request.clienteId())
                .onItem().ifNull().failWith(() ->
                        new BusinessException(
                                ErrorType.CLIENTE_NO_EXISTE,
                                ErrorType.CLIENTE_NO_EXISTE.getDescription()))
                .invoke(cliente -> {
                    if (!cliente.isEstadoActivo()) {
                        throw new BusinessException(
                                ErrorType.CLIENTE_DESACTIVADO,
                                ErrorType.CLIENTE_DESACTIVADO.getDescription());
                    }
                })
                .chain(() -> profesionalRepository.findById(request.profesionalId()))
                .onItem().ifNull().failWith(() ->
                        new BusinessException(
                                ErrorType.PROFESIONAL_NO_EXISTE,
                                ErrorType.PROFESIONAL_NO_EXISTE.getDescription()))
                .invoke(profesional -> {
                    if (!profesional.isEstadoActivo()) {
                        throw new BusinessException(
                                ErrorType.PROFESIONAL_DESACTIVADO,
                                ErrorType.PROFESIONAL_DESACTIVADO.getDescription());
                    }
                })
                .chain(() -> horarioDisponibleRepository.existeHorarioProfesional(
                        request.profesionalId(), request.fecha(), request.horaInicio(), request.horaFin()))
                .invoke(existeHorarioProfesional -> {
                    if (!existeHorarioProfesional) {
                        throw new BusinessException(
                                ErrorType.HORARIO_NO_DISPONIBLE,
                                ErrorType.HORARIO_NO_DISPONIBLE.getDescription());
                    }
                })
                .chain(() -> reservaRepository.existeSolapamientoCreate(
                        request.profesionalId(), request.fecha(), request.horaInicio(), request.horaFin()))
                .invoke(existeSolapamiento -> {
                    if (existeSolapamiento) {
                        throw new BusinessException(
                                ErrorType.RESERVA_SOLAPADA,
                                ErrorType.RESERVA_SOLAPADA.getDescription());
                    }
                })
                .chain(() -> {
                    ReservaEntity entity = reservaMapper.toEntity(request);
                    entity.setEstado(ReservaType.CREADA);
                    return reservaRepository.persist(entity);
                })
                .map(saved -> ApiResponse.<ReservaResponseDto>builder()
                        .data(reservaMapper.toResponse(saved))
                        .statusCode(HttpResponseStatus.CREATED.code())
                        .message("Reserva creada exitosamente")
                        .timestamp(Instant.now())
                        .build());
    }

    @WithTransaction
    @Override
    public Uni<ApiResponse<ReservaResponseDto>> updateReserva(UUID id, ReservaUpdateRequestDto request) {

        if (!request.horaInicio().isBefore(request.horaFin())) {
            return Uni.createFrom().failure(new BusinessException(
                    ErrorType.HORARIO_INVALIDO,
                    ErrorType.HORARIO_INVALIDO.getDescription()));
        }

        return reservaRepository.findById(id)
                .onItem().ifNull().failWith(() ->
                        new BusinessException(
                                ErrorType.RESERVA_NO_EXISTE,
                                ErrorType.RESERVA_NO_EXISTE.getDescription()))
                .invoke(reserva -> {
                    if (reserva.getEstado() != ReservaType.CREADA) {
                        throw new BusinessException(
                                ErrorType.RESERVA_NO_MODIFICABLE,
                                ErrorType.RESERVA_NO_MODIFICABLE.getDescription());
                    }
                })
                .chain(reserva ->
                        horarioDisponibleRepository.existeHorarioProfesional(
                                        reserva.getProfesionalId(), request.fecha(),
                                        request.horaInicio(), request.horaFin())
                                .invoke(existeHorarioProfesional -> {
                                    if (!existeHorarioProfesional) {
                                        throw new BusinessException(
                                                ErrorType.HORARIO_NO_DISPONIBLE,
                                                ErrorType.HORARIO_NO_DISPONIBLE.getDescription());
                                    }
                                })
                                .chain(() -> reservaRepository.existeSolapamientoUpdate(
                                        reserva.getProfesionalId(), request.fecha(),
                                        request.horaInicio(), request.horaFin(), reserva.getId()))
                                .invoke(existeSolapamiento -> {
                                    if (existeSolapamiento) {
                                        throw new BusinessException(
                                                ErrorType.RESERVA_SOLAPADA,
                                                ErrorType.RESERVA_SOLAPADA.getDescription());
                                    }
                                })
                                .replaceWith(reserva))
                .invoke(reserva -> {
                    reserva.setFecha(request.fecha());
                    reserva.setHoraInicio(request.horaInicio());
                    reserva.setHoraFin(request.horaFin());
                })
                .map(reserva -> ApiResponse.<ReservaResponseDto>builder()
                        .data(reservaMapper.toResponse(reserva))
                        .statusCode(HttpResponseStatus.OK.code())
                        .message("Reserva actualizada exitosamente")
                        .timestamp(Instant.now())
                        .build());
    }

    @WithTransaction
    @Override
    public Uni<ApiResponse<ReservaResponseDto>> cancelarReserva(UUID id) {

        return reservaRepository.findById(id)
                .onItem().ifNull().failWith(() ->
                        new BusinessException(
                                ErrorType.RESERVA_NO_EXISTE,
                                ErrorType.RESERVA_NO_EXISTE.getDescription()))
                .invoke(reserva -> {
                    if (reserva.getEstado() != ReservaType.CREADA) {
                        throw new BusinessException(
                                ErrorType.RESERVA_NO_CANCELABLE,
                                ErrorType.RESERVA_NO_CANCELABLE.getDescription());
                    }
                    reserva.setEstado(ReservaType.CANCELADA);
                })
                .map(reserva -> ApiResponse.<ReservaResponseDto>builder()
                        .data(reservaMapper.toResponse(reserva))
                        .statusCode(HttpResponseStatus.OK.code())
                        .message("Reserva cancelada exitosamente")
                        .timestamp(Instant.now())
                        .build());
    }

    @WithSession
    @Override
    public Uni<ApiResponse<List<ProfesionalReservasDto>>> profesionalesPorReservasActivas() {
        return reservaRepository.listarActivas()
                .chain(reservas -> profesionalRepository.listAll()
                        .map(profesionales -> ordenarPorReservasActivas(profesionales, reservas)))
                .map(lista -> ApiResponse.<List<ProfesionalReservasDto>>builder()
                        .data(lista)
                        .statusCode(HttpResponseStatus.OK.code())
                        .message("Se obtuvo correctamente la información requerida.")
                        .totalElements(lista.size())
                        .timestamp(Instant.now())
                        .build());
    }

    /** Lógica pura (sin BD): se puede probar con JUnit sin levantar nada. */
    static List<ProfesionalReservasDto> ordenarPorReservasActivas(
            List<ProfesionalEntity> profesionales, List<ReservaEntity> reservasActivas) {

        Map<UUID, Long> conteo = reservasActivas.stream()
                .collect(Collectors.groupingBy(ReservaEntity::getProfesionalId, Collectors.counting()));

        return profesionales.stream()
                .map(p -> new ProfesionalReservasDto(
                        p.getId(), p.getNombres(), p.getApellidos(), p.getEspecialidad(),
                        conteo.getOrDefault(p.getId(), 0L)))
                .sorted(Comparator.comparingLong(ProfesionalReservasDto::reservasActivas).reversed()
                        .thenComparing(ProfesionalReservasDto::apellidos))
                .toList();
    }

    @WithSession
    @Override
    public Uni<ApiResponse<Map<LocalDate, List<ReservaDetalleDto>>>> reservasPorFecha() {
        return reservaRepository.listarActivas()
                .chain(reservas -> {
                    if (reservas.isEmpty()) {
                        return Uni.createFrom().item(Map.<LocalDate, List<ReservaDetalleDto>>of());
                    }
                    Set<UUID> clienteIds = reservas.stream()
                            .map(ReservaEntity::getClienteId).collect(Collectors.toSet());
                    Set<UUID> profesionalIds = reservas.stream()
                            .map(ReservaEntity::getProfesionalId).collect(Collectors.toSet());

                    return clienteRepository.list("id in ?1", clienteIds)
                            .chain(clientes -> profesionalRepository.list("id in ?1", profesionalIds)
                                    .map(profesionales -> agruparPorFecha(reservas, clientes, profesionales)));
                })
                .map(porFecha -> ApiResponse.<Map<LocalDate, List<ReservaDetalleDto>>>builder()
                        .data(porFecha)
                        .statusCode(HttpResponseStatus.OK.code())
                        .message("Se obtuvo correctamente la información requerida.")
                        .totalElements(porFecha.size())
                        .timestamp(Instant.now())
                        .build());
    }

    static Map<LocalDate, List<ReservaDetalleDto>> agruparPorFecha(
            List<ReservaEntity> reservas,
            List<ClienteEntity> clientes,
            List<ProfesionalEntity> profesionales) {

        Map<UUID, String> nombreCliente = clientes.stream()
                .collect(Collectors.toMap(ClienteEntity::getId,
                        c -> c.getNombres() + " " + c.getApellidos()));

        Map<UUID, String> nombreProfesional = profesionales.stream()
                .collect(Collectors.toMap(ProfesionalEntity::getId,
                        p -> p.getNombres() + " " + p.getApellidos()));

        return reservas.stream()
                .sorted(Comparator.comparing(ReservaEntity::getFecha)
                        .thenComparing(ReservaEntity::getHoraInicio))
                .collect(Collectors.groupingBy(
                        ReservaEntity::getFecha,
                        TreeMap::new,
                        Collectors.mapping(
                                r -> new ReservaDetalleDto(
                                        r.getId(), r.getHoraInicio(), r.getHoraFin(),
                                        nombreCliente.get(r.getClienteId()),
                                        nombreProfesional.get(r.getProfesionalId())),
                                Collectors.toList())));
    }
}
