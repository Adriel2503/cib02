/* ============================================================================
   Soundboxd · 02_datos_ejemplo.sql
   Proyecto grupal CIB02 · Ingeniería de Software · FIEE UNI · Grupo 3, Sección O
   Responsable: Valeria Solís · Rama: bd/esquema

   Datos de ejemplo para desarrollo y pruebas:
     10 artistas · 25 álbumes · 100 canciones principales · 5 usuarios
     (1 MODERADOR) · 15 reseñas · 4 listas (14 álbumes en listas) · 6 seguimientos

   Orden de ejecución: 00 → 01 → 02 → 03
   Debe ejecutarse una sola vez, sobre las tablas vacías creadas por 01_esquema.sql.

   CONTRASEÑAS:
     Las contraseñas NO están en texto plano. Los hashes BCrypt los entregó
     Bryan, generados con su clase PasswordHasher, y se pegan en las variables
     del bloque «HASHES BCRYPT». Bryan entregó 4 hashes: usuario4 usa el mismo
     hash que usuario3 (misma contraseña) hasta que haya un quinto hash.

   Notas:
     - Los IDs se fijan de forma explícita (IDENTITY_INSERT) para que los datos
       sean reproducibles y las relaciones se lean fácil.
     - Las duraciones de las canciones son aproximadas (segundos).
     - Los correos usan el dominio reservado example.com.
   ============================================================================ */

USE SoundboxdDB;
GO

SET NOCOUNT ON;
SET XACT_ABORT ON;

BEGIN TRANSACTION;

/* ---------------------------------------------------------------------------
   HASHES BCRYPT (entregados por Bryan; usuario4 reutiliza el de usuario3)
   --------------------------------------------------------------------------- */
DECLARE @hash_usuario1 VARCHAR(100) = '$2a$12$YJ7IbZApvhz72LpAbFrZuuu9I8ZGQGjRgaKIxIARcDGpVtsdJcRl6';
DECLARE @hash_usuario2 VARCHAR(100) = '$2a$12$iYSKYD8mU.6HWEjfEdKvVeUaRu2LaGqkM685p6ZdXSJgwzLkJ1hDi';
DECLARE @hash_usuario3 VARCHAR(100) = '$2a$12$C5RGH4ed5E53.y8BkFu5q.4p4nB6lZ215118vm50sGJaGWqBVe81y';
DECLARE @hash_moderador1 VARCHAR(100) = '$2a$12$nS8WBMNPMTgU0zOsH1gTfOefVo3GAcTovRaqEqRA0L3E02fTLunLW';

/* ---------------------------------------------------------------------------
   ARTISTA
   --------------------------------------------------------------------------- */
SET IDENTITY_INSERT dbo.ARTISTA ON;
INSERT INTO dbo.ARTISTA (id_artista, nombre, pais) VALUES
    (1, N'Radiohead', N'Reino Unido'),
    (2, N'Daft Punk', N'Francia'),
    (3, N'Kendrick Lamar', N'Estados Unidos'),
    (4, N'Michael Jackson', N'Estados Unidos'),
    (5, N'Pink Floyd', N'Reino Unido'),
    (6, N'Björk', N'Islandia'),
    (7, N'Miles Davis', N'Estados Unidos'),
    (8, N'Nirvana', N'Estados Unidos'),
    (9, N'Fleetwood Mac', N'Reino Unido'),
    (10, N'Amy Winehouse', N'Reino Unido');
SET IDENTITY_INSERT dbo.ARTISTA OFF;

/* ---------------------------------------------------------------------------
   ALBUM
   --------------------------------------------------------------------------- */
SET IDENTITY_INSERT dbo.ALBUM ON;
INSERT INTO dbo.ALBUM (id_album, titulo, id_artista, anio, genero, portada) VALUES
    (1, N'The Bends', 1, 1995, N'Rock alternativo', NULL),
    (2, N'OK Computer', 1, 1997, N'Rock alternativo', NULL),
    (3, N'Kid A', 1, 2000, N'Electrónica experimental', NULL),
    (4, N'In Rainbows', 1, 2007, N'Rock alternativo', NULL),
    (5, N'Homework', 2, 1997, N'Electrónica', NULL),
    (6, N'Discovery', 2, 2001, N'Electrónica', NULL),
    (7, N'Random Access Memories', 2, 2013, N'Disco y electrónica', NULL),
    (8, N'good kid, m.A.A.d city', 3, 2012, N'Hip hop', NULL),
    (9, N'To Pimp a Butterfly', 3, 2015, N'Hip hop', NULL),
    (10, N'DAMN.', 3, 2017, N'Hip hop', NULL),
    (11, N'Off the Wall', 4, 1979, N'Pop', NULL),
    (12, N'Thriller', 4, 1982, N'Pop', NULL),
    (13, N'Bad', 4, 1987, N'Pop', NULL),
    (14, N'The Dark Side of the Moon', 5, 1973, N'Rock progresivo', NULL),
    (15, N'Wish You Were Here', 5, 1975, N'Rock progresivo', NULL),
    (16, N'The Wall', 5, 1979, N'Rock progresivo', NULL),
    (17, N'Homogenic', 6, 1997, N'Electrónica', NULL),
    (18, N'Vespertine', 6, 2001, N'Electrónica', NULL),
    (19, N'Kind of Blue', 7, 1959, N'Jazz', NULL),
    (20, N'Bitches Brew', 7, 1970, N'Jazz fusión', NULL),
    (21, N'Nevermind', 8, 1991, N'Grunge', NULL),
    (22, N'In Utero', 8, 1993, N'Grunge', NULL),
    (23, N'Rumours', 9, 1977, N'Rock', NULL),
    (24, N'Frank', 10, 2003, N'Soul', NULL),
    (25, N'Back to Black', 10, 2006, N'Soul', NULL);
SET IDENTITY_INSERT dbo.ALBUM OFF;

/* ---------------------------------------------------------------------------
   CANCION (canciones principales de cada álbum; duración en segundos)
   --------------------------------------------------------------------------- */
INSERT INTO dbo.CANCION (id_album, titulo, numero_pista, duracion_seg) VALUES
    (1, N'The Bends', 2, 246),
    (1, N'High and Dry', 3, 257),
    (1, N'Fake Plastic Trees', 4, 290),
    (1, N'Street Spirit (Fade Out)', 12, 253),
    (2, N'Airbag', 1, 284),
    (2, N'Paranoid Android', 2, 383),
    (2, N'Karma Police', 6, 261),
    (2, N'No Surprises', 10, 229),
    (3, N'Everything in Its Right Place', 1, 251),
    (3, N'The National Anthem', 3, 351),
    (3, N'How to Disappear Completely', 4, 356),
    (3, N'Idioteque', 8, 309),
    (4, N'15 Step', 1, 237),
    (4, N'Weird Fishes/Arpeggi', 4, 318),
    (4, N'Reckoner', 7, 290),
    (4, N'Videotape', 10, 280),
    (5, N'Da Funk', 4, 328),
    (5, N'Around the World', 7, 429),
    (5, N'Teachers', 9, 172),
    (5, N'Burnin''', 13, 413),
    (6, N'One More Time', 1, 320),
    (6, N'Digital Love', 3, 298),
    (6, N'Harder, Better, Faster, Stronger', 4, 224),
    (6, N'Veridis Quo', 11, 345),
    (7, N'Give Life Back to Music', 1, 274),
    (7, N'Giorgio by Moroder', 3, 544),
    (7, N'Get Lucky', 8, 369),
    (7, N'Contact', 13, 381),
    (8, N'Backseat Freestyle', 3, 212),
    (8, N'Money Trees', 5, 386),
    (8, N'm.A.A.d city', 8, 350),
    (8, N'Swimming Pools (Drank)', 9, 313),
    (9, N'King Kunta', 3, 234),
    (9, N'Alright', 7, 219),
    (9, N'How Much a Dollar Cost', 11, 261),
    (9, N'The Blacker the Berry', 13, 328),
    (10, N'DNA.', 2, 185),
    (10, N'LOYALTY.', 6, 227),
    (10, N'HUMBLE.', 8, 177),
    (10, N'DUCKWORTH.', 14, 248),
    (11, N'Don''t Stop ''til You Get Enough', 1, 365),
    (11, N'Rock with You', 2, 220),
    (11, N'Off the Wall', 5, 226),
    (11, N'She''s Out of My Life', 7, 218),
    (12, N'Wanna Be Startin'' Somethin''', 1, 363),
    (12, N'Thriller', 4, 357),
    (12, N'Beat It', 5, 258),
    (12, N'Billie Jean', 6, 294),
    (13, N'Bad', 1, 247),
    (13, N'The Way You Make Me Feel', 2, 299),
    (13, N'Man in the Mirror', 7, 319),
    (13, N'Smooth Criminal', 10, 257),
    (14, N'Breathe (In the Air)', 2, 163),
    (14, N'Time', 4, 413),
    (14, N'Money', 6, 383),
    (14, N'Us and Them', 7, 469),
    (15, N'Shine On You Crazy Diamond (Parts I–V)', 1, 810),
    (15, N'Welcome to the Machine', 2, 451),
    (15, N'Have a Cigar', 3, 308),
    (15, N'Wish You Were Here', 4, 334),
    (16, N'Another Brick in the Wall, Part 2', 5, 239),
    (16, N'Mother', 6, 332),
    (16, N'Hey You', 14, 280),
    (16, N'Comfortably Numb', 19, 383),
    (17, N'Hunter', 1, 255),
    (17, N'Jóga', 2, 305),
    (17, N'Bachelorette', 4, 311),
    (17, N'All Is Full of Love', 10, 269),
    (18, N'Hidden Place', 1, 330),
    (18, N'Cocoon', 2, 298),
    (18, N'Undo', 4, 338),
    (18, N'Pagan Poetry', 5, 315),
    (19, N'So What', 1, 562),
    (19, N'Freddie Freeloader', 2, 586),
    (19, N'Blue in Green', 3, 337),
    (19, N'All Blues', 4, 693),
    (19, N'Flamenco Sketches', 5, 566),
    (20, N'Pharaoh''s Dance', 1, 1207),
    (20, N'Bitches Brew', 2, 1619),
    (20, N'Spanish Key', 3, 1052),
    (20, N'Miles Runs the Voodoo Down', 5, 841),
    (21, N'Smells Like Teen Spirit', 1, 301),
    (21, N'Come as You Are', 3, 219),
    (21, N'Lithium', 5, 257),
    (21, N'Something in the Way', 12, 232),
    (22, N'Heart-Shaped Box', 3, 281),
    (22, N'Dumb', 6, 152),
    (22, N'Pennyroyal Tea', 9, 218),
    (22, N'All Apologies', 12, 231),
    (23, N'Dreams', 2, 254),
    (23, N'Don''t Stop', 4, 192),
    (23, N'Go Your Own Way', 5, 218),
    (23, N'The Chain', 7, 268),
    (24, N'Intro / Stronger Than Me', 1, 216),
    (24, N'In My Bed', 7, 315),
    (24, N'October Song', 9, 205),
    (25, N'Rehab', 1, 215),
    (25, N'You Know I''m No Good', 2, 257),
    (25, N'Back to Black', 5, 241),
    (25, N'Tears Dry on Their Own', 7, 186);

/* ---------------------------------------------------------------------------
   USUARIO (hash_contrasena viene de las variables de arriba)
   --------------------------------------------------------------------------- */
SET IDENTITY_INSERT dbo.USUARIO ON;
INSERT INTO dbo.USUARIO (id_usuario, nombre_usuario, correo, hash_contrasena, rol, fecha_registro, activo) VALUES
    (1, N'usuario1', N'usuario1@example.com', @hash_usuario1, 'USUARIO', '2026-10-05T09:12:00', 1),
    (2, N'usuario2', N'usuario2@example.com', @hash_usuario2, 'USUARIO', '2026-10-06T14:30:00', 1),
    (3, N'usuario3', N'usuario3@example.com', @hash_usuario3, 'USUARIO', '2026-10-08T18:45:00', 1),
    (4, N'usuario4', N'usuario4@example.com', @hash_usuario3, 'USUARIO', '2026-10-10T11:05:00', 1),
    (5, N'moderador1', N'moderador1@example.com', @hash_moderador1, 'MODERADOR', '2026-10-01T08:00:00', 1);
SET IDENTITY_INSERT dbo.USUARIO OFF;

/* ---------------------------------------------------------------------------
   RESENA (12 publicadas, 1 borrador, 1 reportada, 1 oculta)
   --------------------------------------------------------------------------- */
INSERT INTO dbo.RESENA (id_usuario, id_album, calificacion, texto, estado, fecha) VALUES
    (1, 2, 5, N'Un disco que no envejece: tensión, paranoia y melodías enormes de principio a fin.', 'PUBLICADA', '2026-11-02T10:15:00'),
    (1, 12, 5, N'Pop perfecto: casi cada canción podría haber sido un single.', 'PUBLICADA', '2026-11-03T18:40:00'),
    (1, 19, 4, N'Ideal para estudiar o programar de noche. Muy relajante.', 'PUBLICADA', '2026-11-05T21:05:00'),
    (2, 2, 4, N'Muy bueno, aunque la segunda mitad se me hace más pesada.', 'PUBLICADA', '2026-11-04T09:30:00'),
    (2, 8, 5, N'Cuenta una historia completa y la producción es impecable.', 'PUBLICADA', '2026-11-06T16:20:00'),
    (2, 9, 5, N'Jazz, funk y rap en un solo álbum. Exigente, pero vale la pena.', 'PUBLICADA', '2026-11-07T12:10:00'),
    (3, 4, 5, N'Cálido y lleno de detalles. Se disfruta más con audífonos.', 'PUBLICADA', '2026-11-08T20:00:00'),
    (3, 6, 4, N'Nostalgia electrónica y mucha energía; perfecto para empezar el día.', 'PUBLICADA', '2026-11-09T14:25:00'),
    (3, 21, 4, N'Crudo y directo, con melodías que se quedan en la cabeza.', 'PUBLICADA', '2026-11-10T19:45:00'),
    (4, 19, 5, N'El disco de jazz que todos deberían escuchar al menos una vez.', 'PUBLICADA', '2026-11-11T08:50:00'),
    (4, 14, 5, N'Se disfruta mejor completo y sin interrupciones.', 'PUBLICADA', '2026-11-12T22:15:00'),
    (4, 20, 3, N'Interesante, pero demasiado largo y denso para mi gusto.', 'PUBLICADA', '2026-11-13T17:30:00'),
    (2, 25, 4, N'Voz y producción retro con una emoción muy real.', 'BORRADOR', '2026-11-14T11:00:00'),
    (4, 13, 1, N'Reseña de ejemplo reportada por lenguaje ofensivo; pendiente de revisión.', 'REPORTADA', '2026-11-15T13:20:00'),
    (3, 16, 2, N'Reseña de ejemplo ocultada por moderación.', 'OCULTA', '2026-11-16T15:10:00');

/* ---------------------------------------------------------------------------
   LISTA y LISTA_ALBUM
   --------------------------------------------------------------------------- */
SET IDENTITY_INSERT dbo.LISTA ON;
INSERT INTO dbo.LISTA (id_lista, id_usuario, nombre, descripcion, es_publica, fecha_creacion) VALUES
    (1, 1, N'Imprescindibles del rock alternativo', N'Discos que marcaron el rock de los 90 y los 2000.', 1, '2026-11-01T20:00:00'),
    (2, 2, N'Rap para concentrarse', N'Álbumes de hip hop con letras y producción para escuchar con atención.', 1, '2026-11-02T19:30:00'),
    (3, 3, N'Electrónica para programar', N'Mi lista privada para trabajar sin distracciones.', 0, '2026-11-03T22:10:00'),
    (4, 4, N'Clásicos para escuchar de noche', N'Discos clásicos de jazz y rock para cerrar el día.', 1, '2026-11-04T23:00:00');
SET IDENTITY_INSERT dbo.LISTA OFF;

INSERT INTO dbo.LISTA_ALBUM (id_lista, id_album, posicion) VALUES
    (1, 2, 1),
    (1, 4, 2),
    (1, 21, 3),
    (1, 22, 4),
    (2, 8, 1),
    (2, 9, 2),
    (2, 10, 3),
    (3, 6, 1),
    (3, 7, 2),
    (3, 17, 3),
    (3, 5, 4),
    (4, 19, 1),
    (4, 14, 2),
    (4, 12, 3);

/* ---------------------------------------------------------------------------
   SEGUIMIENTO
   --------------------------------------------------------------------------- */
INSERT INTO dbo.SEGUIMIENTO (id_seguidor, id_seguido, fecha) VALUES
    (1, 2, '2026-10-12T10:00:00'),
    (1, 3, '2026-10-12T10:05:00'),
    (1, 4, '2026-10-13T09:00:00'),
    (2, 1, '2026-10-14T16:20:00'),
    (3, 2, '2026-10-15T12:40:00'),
    (4, 3, '2026-10-16T18:10:00');

COMMIT TRANSACTION;

/* ---------------------------------------------------------------------------
   Resumen: filas por tabla
   --------------------------------------------------------------------------- */

SELECT 'ARTISTA' AS tabla, COUNT(*) AS filas FROM dbo.ARTISTA
UNION ALL SELECT 'ALBUM', COUNT(*) FROM dbo.ALBUM
UNION ALL SELECT 'CANCION', COUNT(*) FROM dbo.CANCION
UNION ALL SELECT 'USUARIO', COUNT(*) FROM dbo.USUARIO
UNION ALL SELECT 'RESENA', COUNT(*) FROM dbo.RESENA
UNION ALL SELECT 'LISTA', COUNT(*) FROM dbo.LISTA
UNION ALL SELECT 'LISTA_ALBUM', COUNT(*) FROM dbo.LISTA_ALBUM
UNION ALL SELECT 'SEGUIMIENTO', COUNT(*) FROM dbo.SEGUIMIENTO;
GO
