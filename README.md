# StockAndesBackend

Backend REST desarrollado para el caso StockAndes - Control de Inventarios,
correspondiente al curso Lenguaje de Programación II.

## Tecnologías utilizadas

- Java 21
- Spring Boot 4
- Spring Data JPA
- Hibernate
- Maven
- Oracle Database
- Lombok
- Bean Validation
- Swagger / OpenAPI
- JUnit 5
- Mockito

## Arquitectura

El proyecto utiliza arquitectura por capas:

Controller -> Service -> Repository -> Oracle

La lógica de negocio se encuentra en la capa Service.

Estructura principal:

src/main/java/com/example/StockAndesBackend/

- controller
- dto
- entity
- enums
- exception
- repository
- service
  - generic
  - service
  - impl

## Requerimientos implementados

### RF01 - Salud de la API

Endpoint:

GET /api/v1/health

Comprueba que la aplicación y la conexión a Oracle se encuentren disponibles.

### RF02 - Categorías

CRUD de categorías:

/api/v1/categorias

El nombre de una categoría debe ser único.

### RF03 - Productos

CRUD de productos:

/api/v1/productos

Cada producto pertenece a una categoría.

También se implementa:

GET /api/v1/categorias/{id}/productos

### RF04 - Áreas

Registro, consulta, actualización y listado de áreas:

/api/v1/areas

Se valida código, nombre y presupuesto mensual.

### RF05 - Despachos

Endpoints:

POST /api/v1/despachos

GET /api/v1/despachos

GET /api/v1/despachos/{id}

PATCH /api/v1/despachos/{id}/anular

El despacho utiliza una estructura cabecera-detalle.

## Reglas de negocio

### RN01

Solo se puede realizar un despacho hacia un área activa y utilizando
productos activos.

### RN02

La cantidad solicitada no puede superar el stock disponible.

Al registrar un despacho se descuenta el stock.

Al anularlo se devuelve el stock.

### RN03

La suma de los despachos registrados durante el mes más el nuevo
despacho no puede superar el presupuesto mensual del área.

### RN04

Un mismo producto no puede repetirse dentro de un despacho.

El importe de cada detalle se calcula:

importe = cantidad * costoUnitario

El monto total del despacho corresponde a la suma de los importes.

## Transacciones

El método de registro de despacho utiliza @Transactional.

Esto permite que si alguna regla de negocio falla se realice rollback
y no queden registrados datos parciales ni modificaciones incorrectas
del stock.

## RF06 - Búsqueda de productos

Endpoint:

GET /api/v1/productos/buscar

Permite utilizar los filtros:

- nombre
- categoriaId
- stockBajo
- orden
- dir

Ejemplo:

GET /api/v1/productos/buscar?categoriaId=1&stockBajo=true&orden=stock&dir=asc

No se utiliza paginación.

## Manejo de errores

La aplicación utiliza GlobalExceptionHandler.

Códigos principales:

400 - Error de validación o solicitud incorrecta

404 - Recurso no encontrado

409 - Incumplimiento de una regla de negocio

500 - Error interno inesperado

## Base de datos

La aplicación utiliza Oracle Database.

Perfil utilizado durante el desarrollo:

dev

Las tablas principales son:

- categorias
- productos
- areas
- despachos
- detalle_despachos

Hibernate crea o actualiza las tablas mediante:

ddl-auto: update

## Ejecución en STS

Abrir Spring Tool Suite.

Importar el proyecto como:

Existing Maven Projects

Seleccionar la carpeta StockAndesBackend.

Verificar que se esté utilizando Java 21.

Ejecutar:

StockAndesBackendApplication.java

Run As -> Spring Boot App

La aplicación se ejecutará en:

http://localhost:8080

## Swagger

La documentación de la API puede consultarse en:

http://localhost:8080/swagger-ui.html

## Pruebas con Postman

Las pruebas pueden realizarse utilizando como URL base:

http://localhost:8080

Se comprobaron:

- health
- categorías
- productos
- áreas
- búsqueda de productos
- registro de despachos
- área inactiva
- stock insuficiente
- control de presupuesto
- producto repetido
- rollback
- anulación de despacho

## Pruebas automatizadas

El proyecto contiene pruebas unitarias para las reglas de despacho.

Para ejecutarlas:

mvn test

Resultado obtenido:

Tests run: 4
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS

## Git

El proyecto utiliza las ramas:

main

develop

feature/pruebas-almonacid

El commit final está identificado mediante la etiqueta:

v1.0-unidad1
