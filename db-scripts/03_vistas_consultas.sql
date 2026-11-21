/* ============================================================================
   Soundboxd · 03_vistas_consultas.sql
   Proyecto grupal CIB02 · Ingeniería de Software · FIEE UNI · Grupo 3, Sección O
   Responsable: Valeria Solís · Rama: bd/esquema

   Contiene:
     1. Índices en las columnas de búsqueda frecuente.
     2. vw_promedio_album: promedio de calificación y número de reseñas
        PUBLICADAS por álbum.
     3. vw_actividad_reciente: reseñas PUBLICADAS con el nombre del usuario y
        del álbum, para el feed de seguidos.
     4. Consultas de ejemplo para el backend (Amado).

   Orden de ejecución: 00 → 01 → 02 → 03
   Se puede volver a ejecutar sin errores (índices y vistas son idempotentes).
   ============================================================================ */

USE SoundboxdDB;
GO

SET NOCOUNT ON;
GO

/* ---------------------------------------------------------------------------
   1. ÍNDICES
   Las claves primarias y las restricciones UNIQUE ya crean sus propios índices.
   Estos cubren búsquedas y uniones frecuentes del backend.
   --------------------------------------------------------------------------- */

-- Búsqueda de álbumes por título
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_ALBUM_titulo' AND object_id = OBJECT_ID(N'dbo.ALBUM'))
    CREATE NONCLUSTERED INDEX IX_ALBUM_titulo ON dbo.ALBUM (titulo);
GO

-- Álbumes de un artista
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_ALBUM_id_artista' AND object_id = OBJECT_ID(N'dbo.ALBUM'))
    CREATE NONCLUSTERED INDEX IX_ALBUM_id_artista ON dbo.ALBUM (id_artista);
GO

-- Reseñas de un álbum (promedios y detalle de álbum)
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_RESENA_id_album' AND object_id = OBJECT_ID(N'dbo.RESENA'))
    CREATE NONCLUSTERED INDEX IX_RESENA_id_album ON dbo.RESENA (id_album) INCLUDE (calificacion, estado);
GO

-- Feed: reseñas publicadas más recientes
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_RESENA_estado_fecha' AND object_id = OBJECT_ID(N'dbo.RESENA'))
    CREATE NONCLUSTERED INDEX IX_RESENA_estado_fecha ON dbo.RESENA (estado, fecha DESC) INCLUDE (id_usuario, id_album, calificacion);
GO

-- Listas de un usuario
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_LISTA_id_usuario' AND object_id = OBJECT_ID(N'dbo.LISTA'))
    CREATE NONCLUSTERED INDEX IX_LISTA_id_usuario ON dbo.LISTA (id_usuario);
GO

-- Listas en las que aparece un álbum
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_LISTA_ALBUM_id_album' AND object_id = OBJECT_ID(N'dbo.LISTA_ALBUM'))
    CREATE NONCLUSTERED INDEX IX_LISTA_ALBUM_id_album ON dbo.LISTA_ALBUM (id_album);
GO

-- Seguidores de un usuario
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_SEGUIMIENTO_id_seguido' AND object_id = OBJECT_ID(N'dbo.SEGUIMIENTO'))
    CREATE NONCLUSTERED INDEX IX_SEGUIMIENTO_id_seguido ON dbo.SEGUIMIENTO (id_seguido);
GO

/* ---------------------------------------------------------------------------
   2. VISTA vw_promedio_album
   Un renglón por álbum (incluye álbumes sin reseñas: promedio NULL y total 0).
   Solo cuentan las reseñas en estado PUBLICADA.
   --------------------------------------------------------------------------- */
CREATE OR ALTER VIEW dbo.vw_promedio_album
AS
SELECT
    a.id_album,
    a.titulo,
    ar.id_artista,
    ar.nombre                                             AS artista,
    a.anio,
    a.genero,
    CAST(AVG(CAST(r.calificacion AS DECIMAL(4, 2))) AS DECIMAL(3, 2)) AS promedio_calificacion,
    COUNT(r.id_resena)                                    AS total_resenas
FROM dbo.ALBUM AS a
INNER JOIN dbo.ARTISTA AS ar
        ON ar.id_artista = a.id_artista
LEFT JOIN dbo.RESENA AS r
       ON r.id_album = a.id_album
      AND r.estado = 'PUBLICADA'
GROUP BY a.id_album, a.titulo, ar.id_artista, ar.nombre, a.anio, a.genero;
GO

/* ---------------------------------------------------------------------------
   3. VISTA vw_actividad_reciente
   Reseñas PUBLICADAS con el nombre del usuario y del álbum.
   La vista no ordena ni filtra por seguidos: el backend usa ORDER BY fecha DESC
   y une con SEGUIMIENTO (ver consulta 2 más abajo).
   --------------------------------------------------------------------------- */
CREATE OR ALTER VIEW dbo.vw_actividad_reciente
AS
SELECT
    r.id_resena,
    r.id_usuario,
    u.nombre_usuario,
    r.id_album,
    a.titulo        AS titulo_album,
    ar.nombre       AS artista,
    r.calificacion,
    r.texto,
    r.fecha
FROM dbo.RESENA AS r
INNER JOIN dbo.USUARIO AS u
        ON u.id_usuario = r.id_usuario
INNER JOIN dbo.ALBUM AS a
        ON a.id_album = r.id_album
INNER JOIN dbo.ARTISTA AS ar
        ON ar.id_artista = a.id_artista
WHERE r.estado = 'PUBLICADA';
GO

PRINT N'Índices y vistas creados (vw_promedio_album, vw_actividad_reciente).';
GO

/* ---------------------------------------------------------------------------
   4. CONSULTAS DE EJEMPLO PARA EL BACKEND
   --------------------------------------------------------------------------- */

-- 4.1 Los álbumes mejor calificados (mínimo 2 reseñas publicadas)
SELECT TOP (10)
       titulo, artista, promedio_calificacion, total_resenas
FROM dbo.vw_promedio_album
WHERE total_resenas >= 2
ORDER BY promedio_calificacion DESC, total_resenas DESC;
GO

-- 4.2 Feed de un usuario: reseñas recientes de las personas que sigue
DECLARE @id_usuario INT = 1;

SELECT TOP (20)
       v.nombre_usuario, v.titulo_album, v.artista, v.calificacion, v.texto, v.fecha
FROM dbo.vw_actividad_reciente AS v
INNER JOIN dbo.SEGUIMIENTO AS s
        ON s.id_seguido = v.id_usuario
WHERE s.id_seguidor = @id_usuario
ORDER BY v.fecha DESC;
GO

-- 4.3 Reseñas más recientes de toda la comunidad
SELECT TOP (10)
       nombre_usuario, titulo_album, calificacion, fecha
FROM dbo.vw_actividad_reciente
ORDER BY fecha DESC;
GO

-- 4.4 Detalle de un álbum: canciones en orden de pista
DECLARE @id_album INT = 2;

SELECT c.numero_pista, c.titulo,
       CONCAT(c.duracion_seg / 60, ':', RIGHT('0' + CAST(c.duracion_seg % 60 AS VARCHAR(2)), 2)) AS duracion
FROM dbo.CANCION AS c
WHERE c.id_album = @id_album
ORDER BY c.numero_pista;
GO

-- 4.5 Álbumes de una lista pública, en el orden definido por el usuario
SELECT l.nombre AS lista, la.posicion, a.titulo, ar.nombre AS artista
FROM dbo.LISTA AS l
INNER JOIN dbo.LISTA_ALBUM AS la ON la.id_lista = l.id_lista
INNER JOIN dbo.ALBUM AS a        ON a.id_album = la.id_album
INNER JOIN dbo.ARTISTA AS ar     ON ar.id_artista = a.id_artista
WHERE l.es_publica = 1
ORDER BY l.id_lista, la.posicion;
GO
