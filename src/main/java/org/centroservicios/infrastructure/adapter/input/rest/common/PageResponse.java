package org.centroservicios.infrastructure.adapter.input.rest.common;

import java.util.List;

public record PageResponse<R>(
        List<R> data,
        int currentPage,
        int limit,
        long totalElements,
        int totalPAges
){}
