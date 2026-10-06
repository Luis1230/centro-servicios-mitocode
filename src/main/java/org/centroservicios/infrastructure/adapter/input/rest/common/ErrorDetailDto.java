package org.centroservicios.infrastructure.adapter.input.rest.common;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorDetailDto(String campo, String error)
{
    public ErrorDetailDto(String error) {
        this(null, error);
    }
}

