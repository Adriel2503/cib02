# Soundboxd

Plataforma social de escritorio para reseñar y listar música (análogo musical de Letterboxd).
Proyecto final del curso **Ingeniería de Software (CIB02)** – FIEE UNI.
Grupo 3 – Sección O. Docente: Ing. M.I.A. Homar Dávila Padilla.

## Integrantes

| Integrante | Rol |
|---|---|
| Valeria Solís | Base de datos |
| Amado Frías | Backend |
| Johan Oscco | Frontend y despliegue |
| Bryan Tapia | Seguridad, cuentas y pruebas |
| David Quispe | Diseño y gestión |

## Tecnologías

- Java, JDK 21
- JavaFX (aplicación de escritorio)
- Patrón MVC
- Microsoft SQL Server vía JDBC (`mssql-jdbc`) con `PreparedStatement`
- IDE: IntelliJ IDEA 2025.3

## Estructura del repositorio

- `/src`: código fuente (model, view, controller)
- `/docs`: documento de sustentación (.docx y .pdf) y archivo `.vpp` de Visual Paradigm
- `/db-backup`: backup de la base de datos

## Backup de la base de datos

Ubicación: `/db-backup`. El archivo `RUTA.txt` indica el enlace de descarga y el nombre exacto del archivo `.bak`.

## Cómo ejecutarlo

Pendiente de completar. Pasos previstos:

1. Crear la base de datos con el script SQL del repositorio.
2. Copiar `src/main/resources/db.properties.example` como `db.properties` y poner las credenciales propias.
3. Abrir el proyecto en IntelliJ IDEA y ejecutar la clase principal.
