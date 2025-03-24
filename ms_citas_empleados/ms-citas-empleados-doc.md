# Documentación del Microservicio de Citas y Empleados

## Descripción General

El Microservicio de Citas y Empleados forma parte del sistema de veterinaria CloudNative y es responsable de gestionar toda la información relacionada con el personal de la veterinaria, sus horarios de trabajo y las citas médicas para las mascotas. Implementa operaciones CRUD (Crear, Leer, Actualizar, Eliminar) para las entidades Empleado, HorarioEmpleado y Cita, proporcionando endpoints RESTful para su integración con el frontend Angular y otros microservicios del sistema.

## Tecnologías Utilizadas

- **Lenguaje**: Java 17
- **Framework**: Spring Boot 3.3.7
- **Persistencia**: Spring Data JPA
- **Base de Datos**: Oracle Cloud
- **Documentación**: Spring HATEOAS
- **Validación**: Jakarta Validation
- **Integración con la Nube**: Azure Functions
- **Dependencias Adicionales**: Lombok, Spring Boot Actuator

## Estructura del Proyecto

```
com.duoc.app_spring
├── config
│   └── CorsConfig.java
├── controller
│   ├── EmpleadoController.java
│   ├── HorarioEmpleadoController.java
│   └── CitaController.java
├── dto
│   ├── EmpleadoDTO.java
│   ├── EmpleadoCreateDTO.java
│   ├── HorarioEmpleadoDTO.java
│   ├── CitaDTO.java
│   ├── CitaCreateDTO.java
│   ├── NotificacionDTO.java
│   └── FacturacionDTO.java
├── model
│   ├── Empleado.java
│   ├── HorarioEmpleado.java
│   └── Cita.java
├── repository
│   ├── EmpleadoRepository.java
│   ├── HorarioEmpleadoRepository.java
│   └── CitaRepository.java
├── service
│   ├── EmpleadoService.java
│   ├── EmpleadoServiceImpl.java
│   ├── HorarioEmpleadoService.java
│   ├── HorarioEmpleadoServiceImpl.java
│   ├── CitaService.java
│   ├── CitaServiceImpl.java
│   ├── AzureFunctionService.java
│   ├── MascotaClienteService.java
│   └── RecordatorioService.java
├── util
│   ├── EmpleadoMapper.java
│   ├── HorarioEmpleadoMapper.java
│   ├── CitaMapper.java
│   └── GlobalExceptionHandler.java
├── App.java
```

## Modelos de Datos

### Empleado

La entidad Empleado representa al personal que trabaja en la veterinaria y contiene los siguientes campos:

| Campo             | Tipo      | Descripción                                          |
|-------------------|-----------|------------------------------------------------------|
| idEmpleado        | Long      | Identificador único del empleado (PK)                |
| rut               | String    | RUT del empleado (UK)                                |
| nombres           | String    | Nombres del empleado                                 |
| apellidos         | String    | Apellidos del empleado                               |
| cargo             | String    | Cargo del empleado en la veterinaria                 |
| especialidad      | String    | Especialidad médica (si aplica)                      |
| email             | String    | Correo electrónico laboral                           |
| telefono          | String    | Teléfono de contacto                                 |
| fechaContratacion | LocalDate | Fecha de contratación                                |
| activo            | Boolean   | Indica si el empleado está activo (true) o no (false)|

### HorarioEmpleado

La entidad HorarioEmpleado representa los horarios de trabajo de los empleados y contiene los siguientes campos:

| Campo      | Tipo      | Descripción                                   |
|------------|-----------|-----------------------------------------------|
| idHorario  | Long      | Identificador único del horario (PK)          |
| idEmpleado | Long      | Identificador del empleado (FK)               |
| diaSemana  | Integer   | Día de la semana (1: Lunes, ..., 7: Domingo)  |
| horaInicio | LocalTime | Hora de inicio de la jornada                  |
| horaFin    | LocalTime | Hora de fin de la jornada                     |

### Cita

La entidad Cita representa una cita médica programada y contiene los siguientes campos:

| Campo      | Tipo          | Descripción                                                    |
|------------|---------------|-----------------------------------------------------------------|
| idCita     | Long          | Identificador único de la cita (PK)                            |
| idMascota  | Long          | Identificador de la mascota (FK)                               |
| idEmpleado | Long          | Identificador del empleado que atiende (FK)                    |
| fechaHora  | LocalDateTime | Fecha y hora programada de la cita                             |
| tipoCita   | String        | Tipo de cita (consulta, vacunación, etc.)                      |
| motivo     | String        | Motivo de la cita                                              |
| estado     | String        | Estado de la cita (programada, completada, cancelada)          |
| notas      | String        | Notas adicionales sobre la cita                                |

## DTOs (Data Transfer Objects)

El microservicio utiliza los siguientes DTOs:

### DTOs Principales

- **EmpleadoDTO**: Representa un empleado completo con todos sus atributos.
- **EmpleadoCreateDTO**: Usado para la creación de nuevos empleados.
- **HorarioEmpleadoDTO**: Representa un horario de empleado con todos sus atributos.
- **CitaDTO**: Representa una cita completa con todos sus atributos.
- **CitaCreateDTO**: Usado para la creación de nuevas citas.

### DTOs para Integración con Azure Functions

- **NotificacionDTO**: Utilizado para enviar notificaciones a los clientes a través de la función Azure de Notificaciones.
  ```java
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public class NotificacionDTO {
      private Long idCliente;
      private String tipo;  // CITA_PROGRAMADA, CITA_COMPLETADA, CITA_CANCELADA, RECORDATORIO_CITA
      private String titulo;
      private String mensaje;
      private LocalDateTime fechaCreacion;
      private String canal; // email, SMS, push
  }
  ```

- **FacturacionDTO**: Utilizado para generar facturas a través de la función Azure de Facturación.
  ```java
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public class FacturacionDTO {
      private Long idCita;
      private Long idCliente;
      private Long idEmpleado;
      private Long idMascota;
      private LocalDateTime fechaEmision;
      private String tipoCita;
      private Double montoEstimado;
  }
  ```

## Servicios para Integración con Azure Functions

### AzureFunctionService

Este servicio se encarga de la comunicación con las funciones de Azure utilizando REST.

```java
@Service
@Slf4j
public class AzureFunctionService {
    private final RestTemplate restTemplate;
    
    @Value("${azure.functions.notificaciones.url}")
    private String notificacionesUrl;
    
    @Value("${azure.functions.facturacion.url}")
    private String facturacionUrl;
    
    // Método para enviar notificaciones
    public void enviarNotificacion(NotificacionDTO notificacion) {
        // Implementación
    }
    
    // Método para generar facturas
    public void generarFactura(Cita cita, Long idCliente) {
        // Implementación
    }
}
```

### MascotaClienteService

Este servicio se comunica con el microservicio de Mascotas para obtener información de los clientes asociados a las mascotas.

```java
@Service
@Slf4j
public class MascotaClienteService {
    private final RestTemplate restTemplate;
    
    @Value("${microservicio.mascotas.url}")
    private String mascotasUrl;
    
    // Método para obtener el ID del cliente asociado a una mascota
    public Long obtenerIdClientePorIdMascota(Long idMascota) {
        // Implementación
    }
}
```

### RecordatorioService

Este servicio programa el envío de recordatorios automáticos para citas del día siguiente.

```java
@Service
@Slf4j
public class RecordatorioService {
    private final CitaRepository citaRepository;
    private final MascotaClienteService mascotaClienteService;
    private final AzureFunctionService azureFunctionService;
    
    // Método programado para ejecutarse diariamente a las 8:00 AM
    @Scheduled(cron = "0 0 8 * * ?")
    public void enviarRecordatoriosCitas() {
        // Implementación
    }
}
```

## Integración con Azure Functions

El microservicio se integra con las siguientes funciones de Azure:

### 1. Función de Notificaciones (for1notificacion)

La función de notificaciones se utiliza para enviar mensajes a los clientes en diferentes eventos relacionados con las citas.

**URL**: `https://for1notificacion.azurewebsites.net/api/HttpNotificaciones`

**Puntos de integración**:
- Al crear una nueva cita: Se envía una notificación al cliente informando sobre la cita programada.
- Al completar una cita: Se envía una notificación al cliente confirmando que la cita ha sido completada.
- Al cancelar una cita: Se envía una notificación al cliente informando que la cita ha sido cancelada.
- Diariamente a las 8:00 AM: Se envían recordatorios para las citas programadas del día siguiente.

**Ejemplo de notificación**:
```json
{
  "idCliente": 1,
  "tipo": "CITA_PROGRAMADA",
  "titulo": "Nueva cita programada",
  "mensaje": "Su cita ha sido programada para el 15/04/2025 10:30. Motivo: Revisión anual",
  "fechaCreacion": "2025-03-23T14:25:30",
  "canal": "email"
}
```

### 2. Función de Facturación (for1facturacion)

La función de facturación se utiliza para generar automáticamente facturas cuando una cita se completa.

**URL**: `https://for1facturacion.azurewebsites.net/api/HttpFacturacion`

**Puntos de integración**:
- Al completar una cita: Se genera una factura por los servicios prestados durante la cita.

**Ejemplo de solicitud de facturación**:
```json
{
  "idCita": 1,
  "idCliente": 1,
  "idEmpleado": 1,
  "idMascota": 1,
  "fechaEmision": "2025-03-23T15:45:30",
  "tipoCita": "Consulta",
  "montoEstimado": 25000.0
}
```

## Configuración de Azure Functions

La configuración de las URL de las funciones de Azure se realiza en el archivo `application.properties`:

```properties
# URLs de Microservicios
microservicio.mascotas.url=http://localhost:8083

# URLs de Funciones Azure
azure.functions.notificaciones.url=https://for1notificacion.azurewebsites.net/api/HttpNotificaciones
azure.functions.facturacion.url=https://for1facturacion.azurewebsites.net/api/HttpFacturacion
```

## API Endpoints

### Endpoints de Citas (con integración de Azure Functions)

#### Crear nueva cita
- **URL**: `/api/citas`
- **Método**: POST
- **Body**: CitaCreateDTO
- **Funcionalidad adicional**: Envía una notificación al cliente a través de la función Azure de Notificaciones.
- **Códigos de Respuesta**:
  - 201: Creada correctamente
  - 400: Datos inválidos
  - 404: Empleado no encontrado

#### Completar cita
- **URL**: `/api/citas/{id}/completar`
- **Método**: PATCH
- **Parámetros**: `id`: ID de la cita a completar
- **Funcionalidad adicional**: 
  - Envía una notificación al cliente a través de la función Azure de Notificaciones.
  - Genera una factura a través de la función Azure de Facturación.
- **Códigos de Respuesta**:
  - 200: OK
  - 400: Estado de cita inválido para completar
  - 404: Cita no encontrada

#### Cancelar cita
- **URL**: `/api/citas/{id}/cancelar`
- **Método**: PATCH
- **Parámetros**: `id`: ID de la cita a cancelar
- **Funcionalidad adicional**: Envía una notificación al cliente a través de la función Azure de Notificaciones.
- **Códigos de Respuesta**:
  - 200: OK
  - 400: Estado de cita inválido para cancelar
  - 404: Cita no encontrada

### Otros endpoints

El resto de los endpoints (Empleados, Horarios y otros endpoints de Citas) permanecen sin cambios respecto a la documentación original.

## Ejemplos de flujos de integración con Azure Functions

### Flujo 1: Creación de una nueva cita

1. El cliente envía una solicitud POST a `/api/citas` con los datos de la cita.
2. El sistema guarda la cita en la base de datos con estado "programada".
3. El sistema obtiene el ID del cliente asociado a la mascota utilizando `MascotaClienteService`.
4. El sistema crea un objeto `NotificacionDTO` con los detalles de la cita.
5. El sistema envía el objeto a la función Azure de Notificaciones utilizando `AzureFunctionService`.
6. La función Azure procesa la notificación y la envía al cliente por el canal especificado.
7. El sistema devuelve la información de la cita creada al cliente.

### Flujo 2: Completar una cita

1. El cliente envía una solicitud PATCH a `/api/citas/{id}/completar`.
2. El sistema verifica que la cita existe y tiene estado "programada".
3. El sistema actualiza el estado de la cita a "completada" en la base de datos.
4. El sistema obtiene el ID del cliente asociado a la mascota.
5. El sistema crea un objeto `NotificacionDTO` con los detalles de la cita completada.
6. El sistema envía el objeto a la función Azure de Notificaciones.
7. El sistema crea un objeto `FacturacionDTO` con los detalles para la facturación.
8. El sistema envía el objeto a la función Azure de Facturación.
9. Las funciones Azure procesan las solicitudes y ejecutan las acciones correspondientes.
10. El sistema devuelve la información de la cita actualizada al cliente.

### Flujo 3: Envío de recordatorios automáticos

1. El sistema ejecuta automáticamente el método `enviarRecordatoriosCitas()` del `RecordatorioService` a las 8:00 AM todos los días.
2. El sistema busca todas las citas programadas para el día siguiente.
3. Para cada cita, el sistema obtiene el ID del cliente asociado a la mascota.
4. El sistema crea un objeto `NotificacionDTO` con los detalles del recordatorio.
5. El sistema envía el objeto a la función Azure de Notificaciones.
6. La función Azure procesa la notificación y la envía al cliente.

## Consideraciones de Implementación

Además de las consideraciones mencionadas en la documentación original:

1. **Integración con Azure Functions**: Las llamadas a las funciones de Azure se realizan de manera asíncrona y no bloquean el flujo principal de la aplicación.
2. **Manejo de errores en las integraciones**: Se implementa un manejo robusto de errores para las llamadas a las funciones de Azure, de modo que los fallos en la comunicación no afecten la funcionalidad principal del microservicio.
3. **Trazabilidad**: Se registran logs detallados de las comunicaciones con las funciones de Azure para facilitar la depuración y el monitoreo.
4. **Configuración de timeouts**: Se establecen tiempos de espera adecuados para las llamadas a servicios externos para evitar bloqueos prolongados.
5. **Fallback para la integración con el microservicio de Mascotas**: Si no se puede obtener el ID del cliente, se utiliza un valor predeterminado para poder enviar notificaciones durante la fase de desarrollo.