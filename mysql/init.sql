-- ============================================================
-- Script de inicializacion de MySQL para el sistema de biblioteca
-- Se ejecuta automaticamente la primera vez que se crea el
-- volumen de MySQL. Crea una base de datos por microservicio
-- y un usuario dedicado con privilegios sobre todas ellas.
-- ============================================================

-- Crear las 11 bases de datos (una por microservicio)
CREATE DATABASE IF NOT EXISTS ms_usuarios    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ms_autores     CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ms_editoriales CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ms_categorias  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ms_libros      CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ms_ejemplares  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ms_sucursales  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ms_prestamos   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ms_reservas    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ms_multas      CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ms_resenas     CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Crear usuario dedicado para los microservicios (si no existe)
-- NOTA: la imagen oficial de MySQL ya crea este usuario con
-- MYSQL_USER/MYSQL_PASSWORD, pero dejamos el CREATE por si acaso.
CREATE USER IF NOT EXISTS 'biblioteca'@'%' IDENTIFIED BY 'biblioteca_pw';

-- Otorgar TODOS los privilegios sobre TODAS las bases ms_*
-- Usamos grants explicitos (no wildcards con backticks, que
-- son tratados como identificadores literales y no patrones).
-- Esto es mas seguro y portable.
GRANT ALL PRIVILEGES ON ms_usuarios.*    TO 'biblioteca'@'%';
GRANT ALL PRIVILEGES ON ms_autores.*     TO 'biblioteca'@'%';
GRANT ALL PRIVILEGES ON ms_editoriales.* TO 'biblioteca'@'%';
GRANT ALL PRIVILEGES ON ms_categorias.*  TO 'biblioteca'@'%';
GRANT ALL PRIVILEGES ON ms_libros.*      TO 'biblioteca'@'%';
GRANT ALL PRIVILEGES ON ms_ejemplares.*  TO 'biblioteca'@'%';
GRANT ALL PRIVILEGES ON ms_sucursales.*  TO 'biblioteca'@'%';
GRANT ALL PRIVILEGES ON ms_prestamos.*   TO 'biblioteca'@'%';
GRANT ALL PRIVILEGES ON ms_reservas.*    TO 'biblioteca'@'%';
GRANT ALL PRIVILEGES ON ms_multas.*      TO 'biblioteca'@'%';
GRANT ALL PRIVILEGES ON ms_resenas.*     TO 'biblioteca'@'%';

-- Asegurar que el root tambien tenga acceso desde cualquier host
ALTER USER 'root'@'localhost' IDENTIFIED BY 'rootpw';

FLUSH PRIVILEGES;
