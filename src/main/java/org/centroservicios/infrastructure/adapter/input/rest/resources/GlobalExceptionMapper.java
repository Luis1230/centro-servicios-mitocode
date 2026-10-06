package org.centroservicios.infrastructure.adapter.input.rest.resources;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.centroservicios.domain.enums.ErrorType;
import org.centroservicios.infrastructure.adapter.input.rest.common.ApiErrorResponse;
import org.centroservicios.infrastructure.adapter.input.rest.common.ErrorDetailDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable throwable) {

        log.error("Error no controlado:{}", throwable.getMessage());
        List<ErrorDetailDto> list = List.of(new ErrorDetailDto(throwable.getMessage()));
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(
                        ApiErrorResponse
                                .builder()
                                .errorId(UUID.randomUUID().toString())
                                .typeError(ErrorType.GENERIC_ERROR.name())
                                .message(ErrorType.GENERIC_ERROR.getDescription())
                                .status(ErrorType.GENERIC_ERROR.getStatus().getStatusCode())
                                .timestamp(LocalDateTime.now())
                                .errorDetails(list)
                                .build()
                ).build();
    }
}