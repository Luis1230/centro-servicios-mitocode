package org.centroservicios.domain.exception;

import lombok.Builder;
import lombok.Getter;
import org.centroservicios.domain.enums.ErrorType;

import java.util.UUID;

@Getter
public class BusinessException extends RuntimeException {

    private final UUID id;
    private final ErrorType type;
    private final String description;

    @Builder
    public BusinessException(
            ErrorType errorType,
            String description
    ) {
        super(description);
        this.id = UUID.randomUUID();
        this.description = errorType.getDescription().formatted(description);
        this.type = errorType;
    }
}
