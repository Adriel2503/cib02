/* ============================================================================
   Soundboxd · Prueba 2: las restricciones rechazan datos inválidos
   (EXTRA para tu verificación; no es uno de los 4 entregables del repositorio)

   Ejecútalo DESPUÉS de 00, 01, 02 y 03. Intenta las operaciones inválidas de la
   guía y muestra una tabla con el resultado de cada una. Todo se deshace al
   final (ROLLBACK), así que no modifica tus datos.

   Resultado esperado: las 6 filas dicen «OK: rechazada ...».
   ============================================================================ */

USE SoundboxdDB;
GO

SET NOCOUNT ON;

DECLARE @r TABLE (n INT, prueba NVARCHAR(120), resultado NVARCHAR(MAX));

BEGIN TRANSACTION;

-- 1a. Reseña con calificacion = 6
BEGIN TRY
    INSERT INTO dbo.RESENA (id_usuario, id_album, calificacion, texto, estado)
    VALUES (3, 1, 6, N'prueba', 'PUBLICADA');
    INSERT INTO @r VALUES (1, N'Reseña con calificacion = 6', N'FALLÓ: se aceptó un dato inválido');
END TRY
BEGIN CATCH
    INSERT INTO @r VALUES (1, N'Reseña con calificacion = 6', N'OK: rechazada. ' + ERROR_MESSAGE());
END CATCH;

-- 1b. Reseña con calificacion = 0
BEGIN TRY
    INSERT INTO dbo.RESENA (id_usuario, id_album, calificacion, texto, estado)
    VALUES (3, 1, 0, N'prueba', 'PUBLICADA');
    INSERT INTO @r VALUES (2, N'Reseña con calificacion = 0', N'FALLÓ: se aceptó un dato inválido');
END TRY
BEGIN CATCH
    INSERT INTO @r VALUES (2, N'Reseña con calificacion = 0', N'OK: rechazada. ' + ERROR_MESSAGE());
END CATCH;

-- 2. Dos reseñas del mismo usuario para el mismo álbum (el usuario 1 ya reseñó el álbum 2)
BEGIN TRY
    INSERT INTO dbo.RESENA (id_usuario, id_album, calificacion, texto, estado)
    VALUES (1, 2, 3, N'segunda reseña', 'PUBLICADA');
    INSERT INTO @r VALUES (3, N'Segunda reseña del mismo usuario y álbum', N'FALLÓ: se aceptó un dato inválido');
END TRY
BEGIN CATCH
    INSERT INTO @r VALUES (3, N'Segunda reseña del mismo usuario y álbum', N'OK: rechazada. ' + ERROR_MESSAGE());
END CATCH;

-- 3. Seguimiento donde id_seguidor = id_seguido
BEGIN TRY
    INSERT INTO dbo.SEGUIMIENTO (id_seguidor, id_seguido) VALUES (1, 1);
    INSERT INTO @r VALUES (4, N'Un usuario se sigue a sí mismo', N'FALLÓ: se aceptó un dato inválido');
END TRY
BEGIN CATCH
    INSERT INTO @r VALUES (4, N'Un usuario se sigue a sí mismo', N'OK: rechazada. ' + ERROR_MESSAGE());
END CATCH;

-- 4a. Usuario con rol = ADMIN
BEGIN TRY
    INSERT INTO dbo.USUARIO (nombre_usuario, correo, hash_contrasena, rol)
    VALUES (N'prueba_admin', N'prueba.admin@example.com', 'x', 'ADMIN');
    INSERT INTO @r VALUES (5, N'Usuario con rol ADMIN', N'FALLÓ: se aceptó un dato inválido');
END TRY
BEGIN CATCH
    INSERT INTO @r VALUES (5, N'Usuario con rol ADMIN', N'OK: rechazada. ' + ERROR_MESSAGE());
END CATCH;

-- 4b. Usuario con un nombre_usuario ya existente
BEGIN TRY
    INSERT INTO dbo.USUARIO (nombre_usuario, correo, hash_contrasena, rol)
    VALUES (N'usuario1', N'otro.correo@example.com', 'x', 'USUARIO');
    INSERT INTO @r VALUES (6, N'Usuario con nombre_usuario repetido', N'FALLÓ: se aceptó un dato inválido');
END TRY
BEGIN CATCH
    INSERT INTO @r VALUES (6, N'Usuario con nombre_usuario repetido', N'OK: rechazada. ' + ERROR_MESSAGE());
END CATCH;

IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;

SELECT n, prueba, resultado FROM @r ORDER BY n;
GO
