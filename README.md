# Centro de Servicios – API de Reservas

Backend REST para el **control de reservas y disponibilidad de profesionales** de un centro de servicios (psicología, mentorías, asesorías, tutorías, etc.). Gestiona profesionales, clientes, horarios disponibles y reservas, aplicando reglas de negocio que evitan solapamientos y validan la disponibilidad real.

## Tecnologías

| Área | Tecnología |
|---|---|
| Lenguaje / Framework | Java 21, Quarkus 3.x |
| Programación reactiva | Mutiny (`Uni` / `Multi`) en endpoints y acceso a datos |
| Persistencia | Hibernate Reactive con Panache y consultas **HQL** |
| Base de datos | SQL Server (esquema `centroservicios`) |
| Migraciones | Flyway (`V1.0.0__Crear_tabla.sql`) |
| Validación | Hibernate Validator (`@Valid`) |
| Documentación | OpenAPI / Swagger UI (SmallRye OpenAPI) |
| Resiliencia | SmallRye Fault Tolerance |
| Mapeo | MapStruct + Lombok |
| Pruebas | `@QuarkusTest`, RestAssured, Mockito |
| Contenedores | Docker (modo JVM y modo nativo con GraalVM/Mandrel) |

## Arquitectura

Arquitectura **hexagonal (DDD ligero)**: el dominio no depende de la infraestructura.

```
org.centroservicios
├── domain
│   ├── enums/          ErrorType, ReservaType
│   ├── exception/      BusinessException
│   └── services/       Puertos de entrada (interfaces de los casos de uso)
├── application/        Casos de uso (implementan los puertos)
└── infrastructure
    └── adapter
        ├── input/rest
        │   ├── common/      ApiResponse, ApiErrorResponse
        │   ├── dto/         Request / Response DTOs
        │   ├── mapper/      MapStruct
        │   ├── resources/   Manejo global de excepciones
        │   └── *Controller  Endpoints REST
        └── output
            ├── entity/      Entidades de persistencia
            └── repository/  Repositorios Panache (HQL)
```

Flujo de una petición: `Controller` → puerto (`domain.services`) → caso de uso (`application`) → repositorio (`output`). Todo el camino devuelve `Uni`, sin bloquear el event loop.

## Reglas de negocio

- **Horarios disponibles**: un profesional no puede tener dos horarios que se solapen el mismo día. Dos intervalos se solapan si `nuevoInicio < existenteFin` y `nuevoFin > existenteInicio`; los horarios "pegados" (uno termina a las 12:00 y el otro empieza a las 12:00) **no** se consideran solapados.
- **Reservas**:
    - Solo se crean si existe un horario disponible que **cubra todo el intervalo** solicitado.
    - Un profesional no puede tener solapamientos con otras reservas activas (estado `CREADA`).
    - Cliente y profesional deben estar activos.
- **Cancelar reserva**: cambia el estado a `CANCELADA`. La disponibilidad se libera automáticamente, porque el control de solapamiento solo considera reservas `CREADA`.
- **Modificar reserva**: solo si está `CREADA`; se revalidan disponibilidad y solapamiento excluyendo a la propia reserva.
- **Consultas** (procesadas en memoria con programación funcional / streams):
    - Profesionales ordenados de forma descendente por número de reservas activas.
    - Reservas agrupadas por fecha (`Map<LocalDate, List<...>>`).

## API REST

Ruta base: `/mitocode/api/v1/centro-servicios`

La documentación interactiva está en Swagger UI (<http://localhost:8080/q/swagger-ui>) y el documento OpenAPI en <http://localhost:8080/q/openapi>.

### Resumen de endpoints

| Recurso | Método | Ruta | Descripción |
|---|---|---|---|
| Profesional | `POST` | `/profesional/create` | Crear profesional |
| Profesional | `GET` | `/profesional` | Listar profesionales activos (paginado) |
| Profesional | `GET` | `/profesional/{busqueda}` | Buscar por nombres y apellidos |
| Cliente | `POST` | `/cliente/create` | Crear cliente |
| Cliente | `GET` | `/cliente` | Listar clientes activos (paginado) |
| Cliente | `GET` | `/cliente/{busqueda}` | Buscar por nombres y apellidos |
| Horario disponible | `POST` | `/horario-disponible/create` | Registrar horario disponible |
| Reserva | `POST` | `/reserva/create` | Registrar reserva |
| Reserva | `PUT` | `/reserva/modificar-reserva/{id}` | Reprogramar una reserva |
| Reserva | `PUT` | `/reserva/cancelar-reserva/{id}` | Cancelar una reserva |
| Reserva | `GET` | `/reserva/profesionales-por-reservas` | Profesionales ordenados por reservas activas (desc.) |
| Reserva | `GET` | `/reserva/reservas-por-fecha` | Reservas activas agrupadas por fecha |

### Convenciones

- Todos los cuerpos van en JSON (`Content-Type: application/json`).
- Fechas en formato `yyyy-MM-dd`, horas en formato `HH:mm:ss`, identificadores en formato UUID.
- Los listados paginados reciben `page` (mínimo 1, por defecto 1) y `limit` (de 10 a 100, por defecto 10).
- Las búsquedas por `{busqueda}` comparan el texto contra "nombres apellidos", sin distinguir mayúsculas y de forma parcial.

### Formato de respuestas

**Éxito** (`ApiResponse`):

```json
{
  "data": { },
  "statusCode": 201,
  "message": "Profesional creado exitosamente",
  "timestamp": "2026-10-05T21:52:37Z"
}
```

Los listados paginados agregan `currentPage`, `totalPages` y `totalElements`.

**Error** (`ErrorResponse`):

```json
{
  "errorId": "d9b2d63d-a231-4ee6-8839-444738734538",
  "typeError": "VALIDATION_ERROR",
  "message": "Los datos enviados no son válidos",
  "status": 400,
  "timestamp": "2026-10-05T21:52:37",
  "errorDetails": [
    { "campo": "nombres", "error": "Los nombres son obligatorios" }
  ]
}
```

El `errorId` también se escribe en los logs, para rastrear un error reportado. `errorDetails` solo aparece cuando hay detalle por campo.

### Errores de negocio

Se lanzan como `BusinessException` y se devuelven con el formato de error anterior. El código HTTP de cada uno lo define el catálogo `ErrorType`.

| `typeError` | Cuándo ocurre |
|---|---|
| `PROFESIONAL_NO_EXISTE` | El profesional indicado no existe |
| `PROFESIONAL_DESACTIVADO` | El profesional está inactivo |
| `HORARIO_NO_DISPONIBLE` | No hay un horario disponible que cubra el intervalo solicitado |
| `RESERVA_SOLAPADA` | El profesional ya tiene una reserva activa que se cruza |
| `RESERVA_NO_EXISTE` | La reserva indicada no existe |
| `RESERVA_NO_MODIFICABLE` | Solo se pueden modificar reservas en estado `CREADA` |

---

### Profesional

#### `POST /profesional/create`

Crea un profesional (queda activo).

```json
{
  "nombres": "Ana Maria",
  "apellidos": "Torres Diaz",
  "especialidad": "Psicología"
}
```

| Campo | Tipo | Obligatorio | Restricciones |
|---|---|---|---|
| `nombres` | string | Sí | No vacío, máx. 100 |
| `apellidos` | string | Sí | No vacío, máx. 100 |
| `especialidad` | string | Sí | No vacío, máx. 100 |

Respuesta `201`:

```json
{
  "data": {
    "id": "178889d3-f3e7-420c-af84-e3a3b5e53cdd",
    "nombres": "Ana Maria",
    "apellidos": "Torres Diaz",
    "especialidad": "Psicología",
    "activo": true
  },
  "statusCode": 201,
  "message": "Profesional creado exitosamente",
  "timestamp": "2026-10-05T21:52:37Z"
}
```

Errores: `400` si falla alguna validación.

#### `GET /profesional?page=1&limit=10`

Lista los profesionales activos, ordenados por apellidos.

| Parámetro | Tipo | Por defecto | Restricciones |
|---|---|---|---|
| `page` | int (query) | 1 | Mínimo 1 |
| `limit` | int (query) | 10 | De 10 a 100 |

Respuesta `200`: `data` es una lista de profesionales, con `currentPage`, `totalPages` y `totalElements`.

#### `GET /profesional/{busqueda}`

Busca profesionales cuyo "nombres apellidos" contenga el texto indicado.

```
GET /mitocode/api/v1/centro-servicios/profesional/torres
```

Respuesta `200`: `data` es una lista de profesionales, con `totalElements`.

---

### Cliente

#### `POST /cliente/create`

Crea un cliente (queda activo). El `email` debe ser único.

```json
{
  "nombres": "Ana Maria",
  "apellidos": "Torres Diaz",
  "email": "luis_xx@gmail.com",
  "telefono": "985881122"
}
```

| Campo | Tipo | Obligatorio | Restricciones |
|---|---|---|---|
| `nombres` | string | Sí | No vacío, máx. 100 |
| `apellidos` | string | Sí | No vacío, máx. 100 |
| `email` | string | Sí | No vacío, máx. 150, único |
| `telefono` | string | Sí | No vacío, máx. 30 |

Respuesta `201`:

```json
{
  "data": {
    "id": "3f2b8c1e-6a4d-4e59-9b7a-1c2d3e4f5a6b",
    "nombres": "Ana Maria",
    "apellidos": "Torres Diaz",
    "email": "luis_xx@gmail.com",
    "telefono": "985881122",
    "activo": true
  },
  "statusCode": 201,
  "message": "Cliente creado exitosamente",
  "timestamp": "2026-10-05T21:52:37Z"
}
```

Errores: `400` si falla alguna validación.

#### `GET /cliente?page=1&limit=10`

Lista los clientes activos, con los mismos parámetros de paginación que profesionales.

#### `GET /cliente/{busqueda}`

Busca clientes cuyo "nombres apellidos" contenga el texto indicado.

---

### Horario disponible

#### `POST /horario-disponible/create`

Registra un horario en el que un profesional atiende. No se permiten horarios que se solapen para el mismo profesional.

```json
{
  "profesionalId": "3f2b8c1e-6a4d-4e59-9b7a-1c2d3e4f5a6b",
  "fecha": "2026-10-05",
  "horaInicio": "09:00:00",
  "horaFin": "12:00:00"
}
```

| Campo | Tipo | Obligatorio | Restricciones |
|---|---|---|---|
| `profesionalId` | UUID | Sí | Debe existir y estar activo |
| `fecha` | date | Sí | `yyyy-MM-dd` |
| `horaInicio` | time | Sí | Menor que `horaFin` |
| `horaFin` | time | Sí | Mayor que `horaInicio` |

Respuesta `201`:

```json
{
  "data": {
    "id": "b2c7e1a4-5d3f-4c8a-9e61-7f0a2d4b8c13",
    "mensaje": "Horario creado exitosamente."
  },
  "statusCode": 201,
  "message": "Horario creado exitosamente.",
  "timestamp": "2026-10-05T21:52:37Z"
}
```

Errores: `400` por validación; `PROFESIONAL_NO_EXISTE`, `PROFESIONAL_DESACTIVADO` y el solapamiento de horarios como errores de negocio.

---

### Reserva

#### `POST /reserva/create`

Registra una reserva en estado `CREADA`. Requiere un horario disponible que cubra todo el intervalo, sin solapamiento con otras reservas activas del profesional, y cliente y profesional activos.

```json
{
  "clienteId": "3f2b8c1e-6a4d-4e59-9b7a-1c2d3e4f5a6b",
  "profesionalId": "8a1d2c3b-4e5f-4a6b-8c7d-9e0f1a2b3c4d",
  "fecha": "2026-10-05",
  "horaInicio": "09:00:00",
  "horaFin": "10:00:00"
}
```

| Campo | Tipo | Obligatorio | Restricciones |
|---|---|---|---|
| `clienteId` | UUID | Sí | Debe existir y estar activo |
| `profesionalId` | UUID | Sí | Debe existir y estar activo |
| `fecha` | date | Sí | `yyyy-MM-dd` |
| `horaInicio` | time | Sí | Menor que `horaFin` |
| `horaFin` | time | Sí | Mayor que `horaInicio` |

Respuesta `201`: `data` contiene la reserva creada (`id`, `fecha`, `horaInicio`, `horaFin`, `clienteId`, `profesionalId`, `estado`).

Errores: `400` por validación; `PROFESIONAL_NO_EXISTE`, `PROFESIONAL_DESACTIVADO`, `HORARIO_NO_DISPONIBLE` y `RESERVA_SOLAPADA` como errores de negocio.

#### `PUT /reserva/modificar-reserva/{id}`

Reprograma una reserva que esté en estado `CREADA`. Se revalidan disponibilidad y solapamiento, excluyendo a la propia reserva.

```json
{
  "fecha": "2026-10-06",
  "horaInicio": "10:00:00",
  "horaFin": "11:00:00"
}
```

| Parámetro / campo | Ubicación | Tipo | Obligatorio |
|---|---|---|---|
| `id` | path | UUID | Sí |
| `fecha` | body | date | Sí |
| `horaInicio` | body | time | Sí |
| `horaFin` | body | time | Sí |

Respuesta `200`: `data` contiene la reserva actualizada.

Errores: `400` por validación; `RESERVA_NO_EXISTE`, `RESERVA_NO_MODIFICABLE`, `HORARIO_NO_DISPONIBLE` y `RESERVA_SOLAPADA` como errores de negocio.

#### `PUT /reserva/cancelar-reserva/{id}`

Cambia el estado de la reserva a `CANCELADA` y libera la disponibilidad. No tiene cuerpo.

```
PUT /mitocode/api/v1/centro-servicios/reserva/cancelar-reserva/5e0c6d1a-3b7f-4a29-8d41-c9f2e7a61b05
```

Respuesta `200`: `data` contiene la reserva con `estado: "CANCELADA"`.

Errores: `RESERVA_NO_EXISTE` si no existe; una reserva que no está `CREADA` no se puede cancelar.

#### `GET /reserva/profesionales-por-reservas`

Lista los profesionales ordenados de forma **descendente por número de reservas activas**. El conteo se procesa en memoria con programación funcional.

Respuesta `200`:

```json
{
  "data": [
    {
      "id": "8a1d2c3b-4e5f-4a6b-8c7d-9e0f1a2b3c4d",
      "nombres": "Luis",
      "apellidos": "Salazar",
      "especialidad": "Psicología",
      "reservasActivas": 2
    },
    {
      "id": "178889d3-f3e7-420c-af84-e3a3b5e53cdd",
      "nombres": "Code",
      "apellidos": "Y",
      "especialidad": "Mentoría",
      "reservasActivas": 1
    }
  ],
  "statusCode": 200,
  "message": "Se obtuvo correctamente la información requerida.",
  "totalElements": 2,
  "timestamp": "2026-10-05T21:52:37Z"
}
```

#### `GET /reserva/reservas-por-fecha`

Muestra las reservas activas agrupadas por fecha (`Map<LocalDate, List<...>>`), con las fechas y las horas en orden ascendente.

Respuesta `200`:

```json
{
  "data": {
    "2026-05-10": [
      { "id": "…", "horaInicio": "09:00:00", "horaFin": "10:00:00",
        "cliente": "Ana Torres", "profesional": "Luis Salazar" },
      { "id": "…", "horaInicio": "10:00:00", "horaFin": "11:00:00",
        "cliente": "Marco Díaz", "profesional": "Luis Salazar" }
    ],
    "2026-05-12": [
      { "id": "…", "horaInicio": "09:00:00", "horaFin": "10:00:00",
        "cliente": "Mito X", "profesional": "Code Y" }
    ]
  },
  "statusCode": 200,
  "message": "Se obtuvo correctamente la información requerida.",
  "totalElements": 2,
  "timestamp": "2026-10-05T21:52:37Z"
}
```

`totalElements` es el número de fechas distintas.

---

## Requisitos previos

- Java 21
- Maven 3.9+ (o el wrapper `mvnw`)
- SQL Server accesible, con la base de datos `CentroServiciosDB` **creada** (Flyway crea el esquema y las tablas, pero no la base)
- Docker (opcional, para ejecutar en contenedor)

## Configuración

Las propiedades están en `src/main/resources/application.properties`. Se pueden sobrescribir con variables de entorno sin recompilar:

| Variable de entorno | Descripción |
|---|---|
| `QUARKUS_DATASOURCE_REACTIVE_URL` | URL reactiva, ej. `sqlserver://localhost:1434/CentroServiciosDB` |
| `QUARKUS_DATASOURCE_JDBC_URL` | URL JDBC (solo la usa Flyway), ej. `jdbc:sqlserver://localhost:1434;databaseName=CentroServiciosDB;encrypt=true;trustServerCertificate=true` |
| `QUARKUS_DATASOURCE_USERNAME` | Usuario de la base de datos |
| `QUARKUS_DATASOURCE_PASSWORD` | Contraseña de la base de datos |

> No subas credenciales reales al repositorio. Usa variables de entorno.

## Ejecución

### Modo desarrollo

```bash
mvn quarkus:dev
```

- API: <http://localhost:8080>
- Swagger UI: <http://localhost:8080/q/swagger-ui>
- Documento OpenAPI: <http://localhost:8080/q/openapi>

### Generar y ejecutar el JAR

```bash
mvn clean package -DskipTests
java -jar target/quarkus-app/quarkus-run.jar
```

Se copia la carpeta `target/quarkus-app` completa, no solo `quarkus-run.jar`.

### Docker – modo JVM

```bash
docker build -f Dockerfile.jvm -t centro-servicios:jvm .

docker run --rm -p 8080:8080 \
  --add-host=host.docker.internal:host-gateway \
  -e QUARKUS_DATASOURCE_REACTIVE_URL="sqlserver://host.docker.internal:1434/CentroServiciosDB" \
  -e QUARKUS_DATASOURCE_JDBC_URL="jdbc:sqlserver://host.docker.internal:1434;databaseName=CentroServiciosDB;encrypt=true;trustServerCertificate=true" \
  -e QUARKUS_DATASOURCE_USERNAME=sa \
  -e QUARKUS_DATASOURCE_PASSWORD="tu_password" \
  centro-servicios:jvm
```

Con `docker compose`, crea un archivo `.env` con `DB_PASSWORD=tu_password` y ejecuta:

```bash
docker compose up --build
```

`host.docker.internal` apunta a la máquina anfitriona, donde corre SQL Server. En Docker Desktop (Windows/Mac) existe por defecto; el `--add-host` solo hace falta en Linux.

### Docker – modo nativo (GraalVM / Mandrel)

```bash
docker build -f Dockerfile.native -t centro-servicios:native .
```

La compilación nativa tarda varios minutos y necesita unos 6-8 GB de memoria para Docker. Requiere `mvnw` y la carpeta `.mvn` en el proyecto. Se ejecuta con el mismo `docker run` de arriba, cambiando la imagen por `centro-servicios:native`.

> Swagger UI no se incluye por defecto en el modo producción (imágenes Docker). Para habilitarlo, agrega `quarkus.swagger-ui.always-include=true`. El documento `/q/openapi` siempre está disponible.

## Pruebas

```bash
mvn test
```

Hay pruebas con `@QuarkusTest` para los endpoints principales (con el servicio simulado mediante `@InjectMock`) y para la lógica de negocio. `@QuarkusTest` arranca la aplicación completa, por lo que necesita la base de datos disponible. Los tests usan el puerto `8081`.

## Colección de Postman

La colección está en `postman/` **(completar con el nombre del archivo)**. Para usarla: *Postman → Import → seleccionar el archivo*. Ajusta la variable de URL base si la API no corre en `http://localhost:8080`.

## Resiliencia

Se usa **SmallRye Fault Tolerance** para proteger **(completar: servicio o método protegido)**:

- `@Timeout`: corta la ejecución si tarda más del límite.
- `@Retry`: reintenta ante fallos transitorios.
- `@CircuitBreaker`: abre el circuito si falla una proporción de llamadas, evitando seguir golpeando un servicio caído.
- `@Fallback`: respuesta alternativa cuando todo lo anterior falla.

Los errores de negocio (`BusinessException`) se excluyen de reintentos, circuit breaker y fallback, porque son esperados y no indican una caída del servicio.

## Logs estructurados

Un filtro JAX-RS (`RequestResponseLoggingFilter`) registra cada petición de entrada y su respuesta de salida en un formato estructurado, con método, ruta, estado y duración. Los errores incluyen su `errorId` para poder correlacionarlos con la respuesta que recibió el cliente.

## Decisiones técnicas

- **Stack 100 % reactivo**: Mutiny + Hibernate Reactive, para no bloquear el event loop. Las transacciones usan `@WithTransaction` y las lecturas `@WithSession`.
- **Dos URLs de base de datos**: Hibernate Reactive usa el cliente reactivo de Vert.x, pero Flyway solo trabaja con JDBC. Por eso se configuran ambas apuntando a la misma base.
- **HQL en lugar de SQL nativo** para consultas y transacciones, como pide el enunciado.
- **Arquitectura hexagonal**: el dominio solo define puertos y reglas; los detalles de HTTP y persistencia viven en los adaptadores.
- **Excepciones propias**: `BusinessException` con un catálogo `ErrorType`, y un manejador global que convierte cualquier excepción en una respuesta uniforme sin exponer detalles internos.
- **Cancelar en lugar de eliminar**: las reservas no se borran, se pasan a `CANCELADA`, conservando el historial.
- **Conteo y agrupación en memoria**: las consultas por reservas traen solo las reservas activas y las procesan con streams (`groupingBy`, `counting`, `sorted`), como pide el enunciado.
- **Migraciones con Flyway**: el esquema se versiona en `src/main/resources/db/migration`, y `quarkus.flyway.migrate-at-start=true` las aplica al arrancar.

## Limitaciones conocidas

- La validación de solapamiento y el guardado no son atómicos: dos peticiones simultáneas idénticas podrían pasar la validación a la vez. Una mejora sería un nivel de aislamiento más estricto o un bloqueo por profesional.
- Una reserva debe quedar cubierta por **un solo** horario disponible: dos horarios contiguos (`09:00-12:00` y `12:00-14:00`) no cubren una reserva de `11:00` a `13:00`.
- Las reservas `COMPLETADA` no se cuentan como activas.
