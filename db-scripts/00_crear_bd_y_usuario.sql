/* ============================================================================
   Soundboxd · 00_crear_bd_y_usuario.sql
   Proyecto grupal CIB02 · Ingeniería de Software · FIEE UNI · Grupo 3, Sección O
   Responsable: Valeria Solís · Rama: bd/esquema

   Qué hace:
     1. Crea la base de datos SoundboxdDB.
     2. Crea el inicio de sesión (login) soundboxd_app con autenticación de
        SQL Server y el usuario del mismo nombre dentro de SoundboxdDB.
     3. Le da permisos solo sobre esta base (lectura y escritura de datos).
        No recibe permisos de administración ni de cambio de estructura.

   Orden de ejecución: 00 → 01 → 02 → 03

   IMPORTANTE (contraseña):
     - En el repositorio este script lleva el marcador CAMBIAR_CLAVE.
     - Antes de ejecutarlo en tu equipo, reemplaza CAMBIAR_CLAVE por una clave
       real (mínimo 8 caracteres, con mayúsculas, minúsculas, números y
       símbolos) SOLO en tu copia local. Nunca subas la clave real al
       repositorio. La clave se comparte con Amado por chat privado.
   ============================================================================ */

/* ---------------------------------------------------------------------------
   OPCIONAL · Reinicio para empezar de cero en una instancia local de pruebas.
   Quita los comentarios de este bloque solo si quieres borrar todo lo anterior.
   ---------------------------------------------------------------------------
USE master;
GO
IF DB_ID(N'SoundboxdDB') IS NOT NULL
BEGIN
    ALTER DATABASE SoundboxdDB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE SoundboxdDB;
END
GO
IF EXISTS (SELECT 1 FROM sys.server_principals WHERE name = N'soundboxd_app')
    DROP LOGIN soundboxd_app;
GO
--------------------------------------------------------------------------- */

USE master;
GO

-- 1. Base de datos ----------------------------------------------------------
IF DB_ID(N'SoundboxdDB') IS NULL
    CREATE DATABASE SoundboxdDB;
GO

-- 2. Inicio de sesión de la aplicación (nivel servidor) ---------------------
IF NOT EXISTS (SELECT 1 FROM sys.server_principals WHERE name = N'soundboxd_app')
    CREATE LOGIN soundboxd_app
        WITH PASSWORD = N'CAMBIAR_CLAVE',
             DEFAULT_DATABASE = SoundboxdDB,
             CHECK_POLICY = ON,
             CHECK_EXPIRATION = OFF;
GO

-- 3. Usuario dentro de la base y permisos mínimos ---------------------------
USE SoundboxdDB;
GO

IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = N'soundboxd_app')
    CREATE USER soundboxd_app FOR LOGIN soundboxd_app;
GO

-- Lectura y escritura de datos (SELECT, INSERT, UPDATE, DELETE) solo en esta base.
ALTER ROLE db_datareader ADD MEMBER soundboxd_app;
ALTER ROLE db_datawriter ADD MEMBER soundboxd_app;
GO

PRINT N'Base SoundboxdDB y usuario soundboxd_app listos. Ejecuta ahora 01_esquema.sql';
GO
