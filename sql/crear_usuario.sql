-- Ejecutar con un usuario administrador de Oracle si todavía no existe STOCKANDES.
-- Si ya tienes un usuario propio, puedes omitir este archivo y cambiar application-dev.yaml.

CREATE USER STOCKANDES IDENTIFIED BY 1234567;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, CREATE TRIGGER, UNLIMITED TABLESPACE TO STOCKANDES;
