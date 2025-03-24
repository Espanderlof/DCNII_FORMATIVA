# Contexto del Proyecto: Sistema de Veterinaria CloudNative

## Descripción General

Se ha desarrollado un sistema informático en la nube para una veterinaria, siguiendo una arquitectura distribuida con microservicios y funciones serverless. El proyecto es parte de una asignatura de Desarrollo Cloud Native II (DSY2207) y ha implementado un prototipo funcional.

## Arquitectura del Sistema

La arquitectura se compone de:

1. **Frontend**: Aplicación Angular hospedada en una Azure VM
2. **BFF (Backend for Frontend)**: AWS API Gateway
3. **Microservicios**: Implementados con Spring Boot y desplegados en contenedores Docker en Azure VMs
4. **Funciones Serverless**: Azure Functions (FaaS)
5. **Base de Datos**: Oracle Cloud relacional
6. **Autenticación**: Azure Active Directory
7. **Mensajería**: RabbitMQ en contenedor Docker

### Diagrama de Arquitectura

```
Frontend - Azure VM
    |
    v
BFF (AWS API Gateway)
    |
    v
+----------------------+          +------------------------+
| Microservicios       |          | Azure Functions (FaaS) |
| - Gestión de Clientes|--------->| - Notificaciones       |
| - Mascotas e Historial|         | - Facturación          |
| - Citas y Empleados  |          | - Integración Labs     |
| - Inventario         |          +------------------------+
+----------------------+                     |
    |                                         v
    v                                 +----------------+
+-------------------+                 | RabbitMQ       |
| Oracle Cloud DB   |<----------------| (Mensajería)   |
+-------------------+                 +----------------+
```

## Base de Datos

La base de datos Oracle Cloud contiene las siguientes tablas:

- CATEGORIAS_INVENTARIO
- CITAS
- CLIENTES
- DETALLES_FACTURA
- EMPLEADOS
- FACTURAS
- HISTORIAL_CLINICO
- HORARIOS_EMPLEADOS
- INVENTARIO
- LABORATORIOS
- MASCOTAS
- MOVIMIENTOS_INVENTARIO
- NOTIFICACIONES
- PAGOS
- RESULTADOS_LABORATORIO
- SOLICITUDES_LABORATORIO
- USUARIOS_SISTEMA

## Secuencias en Base de Datos

Para la generación automática de IDs, se han creado las siguientes secuencias en Oracle:

```sql
CREATE SEQUENCE SEQ_CLIENTE START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_MASCOTA START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_HISTORIAL START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_EMPLEADO START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_HORARIO START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_CITA START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
```

## Microservicios Implementados

Se han implementado completamente los siguientes microservicios:

1. **Microservicio de Inventario**: Gestiona categorías de productos, productos y movimientos de inventario.
2. **Microservicio de Clientes**: Gestión de clientes y sus datos.
3. **Microservicio de Mascotas e Historial Clínico**: Información de mascotas y su historial médico.
4. **Microservicio de Citas y Empleados**: Gestión de agenda, citas y personal.

### Puertos de los Microservicios
- **Microservicio de Inventario**: Puerto 8081
- **Microservicio de Clientes**: Puerto 8082
- **Microservicio de Mascotas e Historial Clínico**: Puerto 8083
- **Microservicio de Citas y Empleados**: Puerto 8084

## Funciones Azure

Se han implementado tres funciones serverless utilizando Azure Functions, todas con endpoints HTTP accesibles:

1. **Notificaciones** (https://for1notificacion.azurewebsites.net/api/HttpNotificaciones):
   - Propósito: Gestionar el envío de notificaciones a clientes vía correo electrónico y SMS.
   - Estado actual: Implementación básica con capacidad para recibir solicitudes HTTP.
   - Métodos soportados: GET y POST.
   - Comportamiento actual: Devuelve el mismo JSON recibido en solicitudes POST.
   - Pendiente: Integración con servicios de email y SMS reales.

2. **Facturación** (https://for1facturacion.azurewebsites.net/api/HttpFacturacion):
   - Propósito: Generar facturas y procesar pagos para los servicios veterinarios.
   - Estado actual: Implementación básica con capacidad para recibir solicitudes HTTP.
   - Métodos soportados: GET y POST.
   - Comportamiento actual: Devuelve el mismo JSON recibido en solicitudes POST.
   - Pendiente: Integración con servicios de pago y generación de documentos PDF.

3. **Integración con Laboratorios** (https://for1laboratorios.azurewebsites.net/api/HttpLaboratorios):
   - Propósito: Comunicación con sistemas externos de laboratorios para envío y recepción de resultados.
   - Estado actual: Implementación básica con capacidad para recibir solicitudes HTTP.
   - Métodos soportados: GET y POST.
   - Comportamiento actual: Devuelve el mismo JSON recibido en solicitudes POST.
   - Pendiente: Integración con APIs de laboratorios externos.

Las tres funciones están desplegadas y son accesibles mediante sus respectivas URLs, pero actualmente sirven como prototipos que responden con los mismos datos que reciben. En las próximas fases del proyecto, se implementará la lógica de negocio específica para cada función y se integrarán con los servicios externos correspondientes.

## Tecnologías Utilizadas

- **Backend**: Java 17, Spring Boot 3.3.7
- **Frontend**: Angular
- **Base de Datos**: Oracle Cloud (SQL)
- **Contenedores**: Docker
- **Nube**: Principalmente Azure, con componentes AWS
- **Autenticación**: Azure AD

## Configuración Actual

El proyecto está configurado con las siguientes dependencias:

- Spring Boot Web
- Spring Boot Data JPA
- Oracle Database JDBC
- Lombok
- Spring Boot Validation
- Spring Boot Actuator
- Spring Boot HATEOAS
- JWT (java-jwt)

## Arquitectura de los Microservicios

Cada microservicio sigue la misma estructura de capas:

1. **Controllers**: API REST para acceso a los recursos
2. **Services**: Lógica de negocio
3. **Repositories**: Acceso a datos
4. **Models**: Entidades JPA
5. **DTOs**: Objetos de transferencia de datos
6. **Utils**: Mappers, manejadores de excepciones y otras utilidades

### Convenciones de Implementación

- Arquitectura en capas: Controller, Service, Repository, Model, DTO
- DTOs para transferencia de datos entre capas
- Manejo centralizado de excepciones
- Pruebas unitarias con JUnit y Mockito
- Documentación de API
- Configuración CORS para permitir peticiones del frontend

## Endpoints Principales

### Microservicio de Clientes
- `GET /api/clientes`: Obtener todos los clientes
- `GET /api/clientes/{id}`: Obtener cliente por ID
- `GET /api/clientes/rut/{rut}`: Obtener cliente por RUT
- `POST /api/clientes`: Crear nuevo cliente
- `PUT /api/clientes/{id}`: Actualizar cliente
- `DELETE /api/clientes/{id}`: Eliminar cliente
- `PATCH /api/clientes/{id}/activar`: Activar cliente
- `PATCH /api/clientes/{id}/desactivar`: Desactivar cliente

### Microservicio de Mascotas e Historial Clínico
- `GET /api/mascotas`: Obtener todas las mascotas
- `GET /api/mascotas/{id}`: Obtener mascota por ID
- `GET /api/mascotas/cliente/{idCliente}`: Obtener mascotas por cliente
- `POST /api/mascotas`: Crear nueva mascota
- `PUT /api/mascotas/{id}`: Actualizar mascota
- `DELETE /api/mascotas/{id}`: Eliminar mascota
- `GET /api/historial-clinico/mascota/{idMascota}`: Obtener historial de una mascota
- `POST /api/historial-clinico`: Crear registro clínico

### Microservicio de Citas y Empleados
- `GET /api/empleados`: Obtener todos los empleados
- `GET /api/empleados/{id}`: Obtener empleado por ID
- `POST /api/empleados`: Crear nuevo empleado
- `PUT /api/empleados/{id}`: Actualizar empleado
- `GET /api/horarios/empleado/{idEmpleado}`: Obtener horarios de un empleado
- `POST /api/horarios`: Crear horario
- `GET /api/citas`: Obtener todas las citas
- `GET /api/citas/mascota/{idMascota}`: Obtener citas por mascota
- `GET /api/citas/empleado/{idEmpleado}`: Obtener citas por empleado
- `POST /api/citas`: Crear nueva cita
- `PATCH /api/citas/{id}/completar`: Marcar cita como completada
- `PATCH /api/citas/{id}/cancelar`: Cancelar cita

## Validaciones Implementadas

- **RUT**: Formato chileno (XX.XXX.XXX-X)
- **Email**: Validación de formato correcto
- **Fechas**: Validación de coherencia en rangos
- **Estado de citas**: Flujo controlado (programada → completada/cancelada)
- **Relaciones entre entidades**: Verificación de existencia

## Próximos Pasos

1. Implementar las funciones de Azure para notificaciones, facturación e integración con laboratorios
2. Integrar los microservicios con el BFF (AWS API Gateway)
3. Desarrollar el frontend en Angular
4. Implementar autenticación mediante Azure AD
5. Realizar pruebas de integración entre todos los componentes
6. Configurar entornos de desarrollo, pruebas y producción
7. Implementar monitoreo y observabilidad
