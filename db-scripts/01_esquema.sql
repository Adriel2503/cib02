/* ============================================================================
   Soundboxd · 01_esquema.sql
   Proyecto grupal CIB02 · Ingeniería de Software · FIEE UNI · Grupo 3, Sección O
   Responsable: Valeria Solís · Rama: bd/esquema

   Crea las 8 tablas de SoundboxdDB con sus claves, restricciones y reglas de
   borrado. Modelo base de la Semana 10.

   Orden de ejecución: 00 → 01 → 02 → 03

   REGLAS DE BORRADO (también documentadas en docs/diccionario-datos.docx)
   ---------------------------------------------------------------------------
   Al eliminar...      Qué pasa
   ------------------  -------------------------------------------------------
   un USUARIO          Se eliminan en cascada sus reseñas, sus listas (y los
                       álbumes de esas listas) y los seguimientos en los que
                       él es el seguidor. Si otros usuarios lo siguen, el
                       borrado se BLOQUEA (ver nota de SEGUIMIENTO).
   un ALBUM            Se eliminan en cascada sus canciones, sus reseñas y sus
                       apariciones en listas.
   un ARTISTA          Se BLOQUEA si todavía tiene álbumes. Primero deben
                       eliminarse (o reasignarse) sus álbumes.
   una LISTA           Se eliminan en cascada sus filas de LISTA_ALBUM.

   NOTA sobre SEGUIMIENTO: SQL Server no permite dos rutas de cascada desde
   USUARIO hacia la misma tabla. Por eso id_seguidor lleva ON DELETE CASCADE
   e id_seguido lleva NO ACTION. En la aplicación se recomienda desactivar la
   cuenta (activo = 0) en lugar de borrarla físicamente.
   ============================================================================ */

USE SoundboxdDB;
GO

SET NOCOUNT ON;
GO

/* ---------------------------------------------------------------------------
   1. USUARIO
   --------------------------------------------------------------------------- */
CREATE TABLE dbo.USUARIO (
    id_usuario       INT           IDENTITY(1,1) NOT NULL,
    nombre_usuario   NVARCHAR(50)  NOT NULL,
    correo           NVARCHAR(254) NOT NULL,
    hash_contrasena  VARCHAR(100)  NOT NULL,                  -- hash BCrypt (60 caracteres)
    rol              VARCHAR(10)   NOT NULL
        CONSTRAINT DF_USUARIO_rol DEFAULT ('USUARIO'),
    fecha_registro   DATETIME2(0)  NOT NULL
        CONSTRAINT DF_USUARIO_fecha_registro DEFAULT (SYSDATETIME()),
    activo           BIT           NOT NULL
        CONSTRAINT DF_USUARIO_activo DEFAULT (1),
    CONSTRAINT PK_USUARIO PRIMARY KEY (id_usuario),
    CONSTRAINT UQ_USUARIO_nombre_usuario UNIQUE (nombre_usuario),
    CONSTRAINT UQ_USUARIO_correo UNIQUE (correo),
    CONSTRAINT CK_USUARIO_rol CHECK (rol IN ('USUARIO', 'MODERADOR'))
);
GO

/* ---------------------------------------------------------------------------
   2. ARTISTA
   --------------------------------------------------------------------------- */
CREATE TABLE dbo.ARTISTA (
    id_artista  INT           IDENTITY(1,1) NOT NULL,
    nombre      NVARCHAR(150) NOT NULL,
    pais        NVARCHAR(60)  NULL,
    CONSTRAINT PK_ARTISTA PRIMARY KEY (id_artista)
);
GO

/* ---------------------------------------------------------------------------
   3. ALBUM
   --------------------------------------------------------------------------- */
CREATE TABLE dbo.ALBUM (
    id_album    INT           IDENTITY(1,1) NOT NULL,
    titulo      NVARCHAR(200) NOT NULL,
    id_artista  INT           NOT NULL,
    anio        SMALLINT      NOT NULL,
    genero      NVARCHAR(60)  NULL,
    portada     NVARCHAR(500) NULL,                           -- URL o ruta de la imagen
    CONSTRAINT PK_ALBUM PRIMARY KEY (id_album),
    CONSTRAINT FK_ALBUM_ARTISTA FOREIGN KEY (id_artista)
        REFERENCES dbo.ARTISTA (id_artista) ON DELETE NO ACTION,
    CONSTRAINT CK_ALBUM_anio CHECK (anio BETWEEN 1900 AND 2100)
);
GO

/* ---------------------------------------------------------------------------
   4. CANCION
   --------------------------------------------------------------------------- */
CREATE TABLE dbo.CANCION (
    id_cancion    INT           IDENTITY(1,1) NOT NULL,
    id_album      INT           NOT NULL,
    titulo        NVARCHAR(200) NOT NULL,
    numero_pista  SMALLINT      NOT NULL,
    duracion_seg  INT           NOT NULL,
    CONSTRAINT PK_CANCION PRIMARY KEY (id_cancion),
    CONSTRAINT FK_CANCION_ALBUM FOREIGN KEY (id_album)
        REFERENCES dbo.ALBUM (id_album) ON DELETE CASCADE,
    CONSTRAINT UQ_CANCION_album_pista UNIQUE (id_album, numero_pista),
    CONSTRAINT CK_CANCION_numero_pista CHECK (numero_pista > 0),
    CONSTRAINT CK_CANCION_duracion CHECK (duracion_seg > 0)
);
GO

/* ---------------------------------------------------------------------------
   5. RESENA
   --------------------------------------------------------------------------- */
CREATE TABLE dbo.RESENA (
    id_resena     INT            IDENTITY(1,1) NOT NULL,
    id_usuario    INT            NOT NULL,
    id_album      INT            NOT NULL,
    calificacion  TINYINT        NOT NULL,
    texto         NVARCHAR(2000) NULL,
    estado        VARCHAR(10)    NOT NULL
        CONSTRAINT DF_RESENA_estado DEFAULT ('PUBLICADA'),
    fecha         DATETIME2(0)   NOT NULL
        CONSTRAINT DF_RESENA_fecha DEFAULT (SYSDATETIME()),
    CONSTRAINT PK_RESENA PRIMARY KEY (id_resena),
    CONSTRAINT FK_RESENA_USUARIO FOREIGN KEY (id_usuario)
        REFERENCES dbo.USUARIO (id_usuario) ON DELETE CASCADE,
    CONSTRAINT FK_RESENA_ALBUM FOREIGN KEY (id_album)
        REFERENCES dbo.ALBUM (id_album) ON DELETE CASCADE,
    CONSTRAINT UQ_RESENA_usuario_album UNIQUE (id_usuario, id_album),
    CONSTRAINT CK_RESENA_calificacion CHECK (calificacion BETWEEN 1 AND 5),
    CONSTRAINT CK_RESENA_estado CHECK (estado IN ('BORRADOR', 'PUBLICADA', 'REPORTADA', 'OCULTA'))
);
GO

/* ---------------------------------------------------------------------------
   6. LISTA
   --------------------------------------------------------------------------- */
CREATE TABLE dbo.LISTA (
    id_lista        INT           IDENTITY(1,1) NOT NULL,
    id_usuario      INT           NOT NULL,
    nombre          NVARCHAR(100) NOT NULL,
    descripcion     NVARCHAR(500) NULL,
    es_publica      BIT           NOT NULL
        CONSTRAINT DF_LISTA_es_publica DEFAULT (0),
    fecha_creacion  DATETIME2(0)  NOT NULL
        CONSTRAINT DF_LISTA_fecha_creacion DEFAULT (SYSDATETIME()),
    CONSTRAINT PK_LISTA PRIMARY KEY (id_lista),
    CONSTRAINT FK_LISTA_USUARIO FOREIGN KEY (id_usuario)
        REFERENCES dbo.USUARIO (id_usuario) ON DELETE CASCADE
);
GO

/* ---------------------------------------------------------------------------
   7. LISTA_ALBUM
   --------------------------------------------------------------------------- */
CREATE TABLE dbo.LISTA_ALBUM (
    id_lista   INT NOT NULL,
    id_album   INT NOT NULL,
    posicion   INT NOT NULL,
    CONSTRAINT PK_LISTA_ALBUM PRIMARY KEY (id_lista, id_album),
    CONSTRAINT FK_LISTA_ALBUM_LISTA FOREIGN KEY (id_lista)
        REFERENCES dbo.LISTA (id_lista) ON DELETE CASCADE,
    CONSTRAINT FK_LISTA_ALBUM_ALBUM FOREIGN KEY (id_album)
        REFERENCES dbo.ALBUM (id_album) ON DELETE CASCADE,
    CONSTRAINT CK_LISTA_ALBUM_posicion CHECK (posicion >= 1)
);
GO

/* ---------------------------------------------------------------------------
   8. SEGUIMIENTO
   --------------------------------------------------------------------------- */
CREATE TABLE dbo.SEGUIMIENTO (
    id_seguidor  INT          NOT NULL,
    id_seguido   INT          NOT NULL,
    fecha        DATETIME2(0) NOT NULL
        CONSTRAINT DF_SEGUIMIENTO_fecha DEFAULT (SYSDATETIME()),
    CONSTRAINT PK_SEGUIMIENTO PRIMARY KEY (id_seguidor, id_seguido),
    CONSTRAINT FK_SEGUIMIENTO_seguidor FOREIGN KEY (id_seguidor)
        REFERENCES dbo.USUARIO (id_usuario) ON DELETE CASCADE,
    CONSTRAINT FK_SEGUIMIENTO_seguido FOREIGN KEY (id_seguido)
        REFERENCES dbo.USUARIO (id_usuario) ON DELETE NO ACTION,
    CONSTRAINT CK_SEGUIMIENTO_distintos CHECK (id_seguidor <> id_seguido)
);
GO

PRINT N'Esquema creado: 8 tablas. Ejecuta ahora 02_datos_ejemplo.sql';
GO
