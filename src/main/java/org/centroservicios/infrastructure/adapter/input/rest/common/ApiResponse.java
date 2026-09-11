package org.centroservicios.infrastructure.adapter.input.rest.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Builder
public class ApiResponse<T> {

    private Integer statusCode;
    private String message;
    private T data;
    private Instant timestamp;
    private Integer currentPage;
    private Integer totalPages;
    private Integer totalElements;
}
