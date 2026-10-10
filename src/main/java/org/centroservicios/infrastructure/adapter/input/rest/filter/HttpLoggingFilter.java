package org.centroservicios.infrastructure.adapter.input.rest.filter;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION - 1)
public class HttpLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static String METHOD = "";
    private static String PATH = "";
    private static long REQUEST_START_TIME_NANOS = 0;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {

        REQUEST_START_TIME_NANOS = System.nanoTime();
        METHOD = containerRequestContext.getMethod();
        PATH = containerRequestContext.getUriInfo().getAbsolutePath().toString();

        // Omitir la validación para el openapi/swagger
        if (PATH.startsWith("q/")) {
            return;
        }

        log.info("[INICIO] --> method:{}, path:{}", METHOD, PATH);
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {

        int statusCode = containerResponseContext.getStatus();
        log.info("[FIN] --> path:{}, method:{}, statusCode:{}, duration(ms):{}",
                PATH, METHOD, statusCode, durationInMs());
    }

    private long durationInMs() {

        return (System.nanoTime() - REQUEST_START_TIME_NANOS) / 1_000_000;
    }
}
