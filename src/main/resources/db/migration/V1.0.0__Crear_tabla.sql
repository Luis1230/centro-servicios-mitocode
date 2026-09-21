-- =============================================
-- TABLA: PROFESIONAL
-- =============================================
CREATE TABLE centroservicios.profesional (
    id UNIQUEIDENTIFIER NOT NULL,
    nombres NVARCHAR(100) NOT NULL,
    apellidos NVARCHAR(100) NOT NULL,
    especialidad NVARCHAR(150) NOT NULL,
    estado_activo BIT NOT NULL DEFAULT 1,

    CONSTRAINT PK_profesional PRIMARY KEY (id)
);
GO


-- =============================================
-- TABLA: CLIENTE
-- =============================================
CREATE TABLE centroservicios.cliente (
    id UNIQUEIDENTIFIER NOT NULL,
    nombres NVARCHAR(100) NOT NULL,
    apellidos NVARCHAR(100) NOT NULL,
    email NVARCHAR(150) NOT NULL,
    telefono NVARCHAR(30) NULL,
    estado_activo BIT NOT NULL DEFAULT 1,

    CONSTRAINT PK_cliente PRIMARY KEY (id),
    CONSTRAINT UQ_cliente_email UNIQUE (email)
);
GO


-- =============================================
-- TABLA: HORARIO DISPONIBLE
-- =============================================
CREATE TABLE centroservicios.horario_disponible (
    id UNIQUEIDENTIFIER NOT NULL,
    profesional_id UNIQUEIDENTIFIER NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado BIT NOT NULL DEFAULT 1,

    CONSTRAINT PK_horario_disponible PRIMARY KEY (id),

    CONSTRAINT FK_horario_profesional
        FOREIGN KEY (profesional_id)
        REFERENCES centroservicios.profesional(id),

    CONSTRAINT CK_horario_horas
        CHECK (hora_inicio < hora_fin)
);
GO


-- =============================================
-- TABLA: RESERVA
-- =============================================
CREATE TABLE centroservicios.reserva (
    id UNIQUEIDENTIFIER NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,

    cliente_id UNIQUEIDENTIFIER NOT NULL,
    profesional_id UNIQUEIDENTIFIER NOT NULL,

    estado VARCHAR(20) NOT NULL DEFAULT 'CREADA',

    CONSTRAINT PK_reserva PRIMARY KEY (id),

    CONSTRAINT FK_reserva_cliente
        FOREIGN KEY (cliente_id)
        REFERENCES centroservicios.cliente(id),

    CONSTRAINT FK_reserva_profesional
        FOREIGN KEY (profesional_id)
        REFERENCES centroservicios.profesional(id),

    CONSTRAINT CK_reserva_horas
        CHECK (hora_inicio < hora_fin),

    CONSTRAINT CK_reserva_estado
        CHECK (estado IN ('CREADA', 'CANCELADA', 'COMPLETADA'))
);
GO