# Documentación del Microservicio de Mascotas e Historial Clínico

## Descripción General

El Microservicio de Mascotas e Historial Clínico forma parte del sistema de veterinaria CloudNative y es responsable de gestionar toda la información relacionada con las mascotas de los clientes y su historial médico. Implementa operaciones CRUD (Crear, Leer, Actualizar, Eliminar) para las entidades Mascota e HistorialClinico, proporcionando endpoints RESTful para su integración con el frontend Angular y otros microservicios del sistema. Además, se integra con Azure Functions para enviar notificaciones y recordatorios a los clientes.

## Tecnologías Utilizadas

- **Lenguaje**: Java 17
- **Framework**: Spring Boot 3.3.7
- **Persistencia**: Spring Data JPA
- **Base de Datos**: Oracle Cloud
- **Documentación**: Spring HATEOAS
- **Validación**: Jakarta Validation
- **Integración en la Nube**: Azure Functions
- **Dependencias Adicionales**: Lombok, Spring Boot Actuator, RestTemplate

## Estructura del Proyecto

```
com.duoc.app_spring
├── config
│   └── CorsConfig.java
├── controller
│   ├── MascotaController.java
│   ├── HistorialClinicoController.java
│   └── RecordatorioController.java (nuevo)
├── dto
│   ├── MascotaDTO.java
│   ├── MascotaCreateDTO.java
│   ├── HistorialClinicoDTO.java
│   ├── HistorialClinicoCreateDTO.java
│   └── NotificacionDTO.java (nuevo)
├── model
│   ├── Mascota.java
│   └── HistorialClinico.java
├── repository
│   ├── MascotaRepository.java
│   └── HistorialClinicoRepository.java
├── service
│   ├── MascotaService.java
│   ├── MascotaServiceImpl.java
│   ├── HistorialClinicoService.java
│   ├── HistorialClinicoServiceImpl.java
│   ├── AzureFunctionService.java (nuevo)
│   ├── ClienteService.java (nuevo)
│   └── RecordatorioVacunacionService.java (nuevo)
├── util
│   ├── MascotaMapper.java
│   ├── HistorialClinicoMapper.java
│   └── GlobalExceptionHandler.java
├── App.java
```

## Modelos de Datos

### Mascota

La entidad Mascota representa a un animal registrado en la veterinaria y contiene los siguientes campos:

| Campo           | Tipo      | Descripción                                          |
|-----------------|-----------|------------------------------------------------------|
| idMascota       | Long      | Identificador único de la mascota (PK)               |
| idCliente       | Long      | Identificador del cliente dueño (FK)                 |
| nombre          | String    | Nombre de la mascota                                 |
| especie         | String    | Especie (perro, gato, etc.)                          |
| raza            | String    | Raza de la mascota                                   |
| fechaNacimiento | LocalDate | Fecha de nacimiento o aproximada                     |
| sexo            | String    | Sexo (M: macho, H: hembra)                           |
| color           | String    | Color del pelaje o plumaje                           |
| peso            | Double    | Peso en kg                                           |
| activo          | Boolean   | Indica si la mascota está activa (true) o no (false) |
| fechaRegistro   | LocalDate | Fecha de registro en el sistema                      |

### HistorialClinico

La entidad HistorialClinico representa un registro médico de una mascota y contiene los siguientes campos:

| Campo          | Tipo      | Descripción                                   |
|----------------|-----------|-----------------------------------------------|
| idHistorial    | Long      | Identificador único del registro clínico (PK) |
| idMascota      | Long      | Identificador de la mascota (FK)              |
| idCita         | Long      | Identificador de la cita relacionada (FK)     |
| idEmpleado     | Long      | Identificador del empleado que atendió (FK)   |
| fechaConsulta  | LocalDate | Fecha de la consulta                          |
| motivoConsulta | String    | Motivo de la consulta                         |
| diagnostico    | String    | Diagnóstico emitido                           |
| tratamiento    | String    | Tratamiento indicado                          |
| observaciones  | String    | Observaciones adicionales                     |

## DTOs (Data Transfer Objects)

El microservicio utiliza los siguientes DTOs:

### MascotaDTO y MascotaCreateDTO
Representan una mascota completa con todos sus atributos, usados tanto para respuestas como para operaciones de creación y actualización.

### HistorialClinicoDTO y HistorialClinicoCreateDTO
Representan un registro de historial clínico completo con todos sus atributos, usados tanto para respuestas como para operaciones de creación y actualización.

### NotificacionDTO (Nuevo)
Utilizado para enviar notificaciones a través de la función Azure de Notificaciones.

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDTO {
    private Long idCliente;
    private String tipo;  // HISTORIAL_CREADO, RECORDATORIO_VACUNA
    private String titulo;
    private String mensaje;
    private LocalDateTime fechaCreacion;
    private String canal; // email, SMS, push
}
```

## Integración con Azure Functions

### AzureFunctionService

Este servicio se encarga de la comunicación con las funciones de Azure utilizando REST.

```java
@Service
public class AzureFunctionService {
    private static final Logger log = LoggerFactory.getLogger(AzureFunctionService.class);
    
    private final RestTemplate restTemplate;
    
    @Value("${azure.functions.notificaciones.url:https://for1notificacion.azurewebsites.net/api/HttpNotificaciones}")
    private String notificacionesUrl;
    
    public AzureFunctionService() {
        this.restTemplate = new RestTemplate();
    }
    
    public void enviarNotificacion(NotificacionDTO notificacion) {
        try {
            log.info("Enviando notificación a la función Azure: {}", notificacion);
            ResponseEntity<String> response = restTemplate.postForEntity(notificacionesUrl, notificacion, String.class);
            log.info("Respuesta de la función Azure: {}", response.getBody());
        } catch (Exception e) {
            log.error("Error al enviar notificación a la función Azure", e);
            // No lanzamos excepción para no interrumpir el flujo principal
        }
    }
}
```

### ClienteService

Este servicio se utiliza para obtener información de los clientes a partir de los IDs de mascotas.

```java
@Service
public class ClienteService {
    private static final Logger log = LoggerFactory.getLogger(ClienteService.class);
    
    private final RestTemplate restTemplate;
    
    @Value("${microservicio.clientes.url:http://20.62.8.211:8082}")
    private String clientesUrl;
    
    public ClienteService() {
        this.restTemplate = new RestTemplate();
    }
    
    public Long obtenerIdClientePorIdMascota(Long idMascota) {
        try {
            // Obtenemos la mascota para saber su idCliente
            String url = clientesUrl + "/api/mascotas/" + idMascota;
            ResponseEntity<MascotaDTO> response = restTemplate.getForEntity(url, MascotaDTO.class);
            MascotaDTO mascota = response.getBody();
            
            if (mascota != null) {
                return mascota.getIdCliente();
            }
            return null;
        } catch (Exception e) {
            log.error("Error al obtener información del cliente por mascota", e);
            return null;
        }
    }
}
```

### RecordatorioVacunacionService

Este servicio implementa la funcionalidad de enviar recordatorios automáticos de vacunación.

```java
@Service
public class RecordatorioVacunacionService {
    private static final Logger log = LoggerFactory.getLogger(RecordatorioVacunacionService.class);

    private final HistorialClinicoRepository historialRepository;
    private final MascotaRepository mascotaRepository;
    private final AzureFunctionService azureFunctionService;

    @Autowired
    public RecordatorioVacunacionService(
            HistorialClinicoRepository historialRepository,
            MascotaRepository mascotaRepository,
            AzureFunctionService azureFunctionService) {
        this.historialRepository = historialRepository;
        this.mascotaRepository = mascotaRepository;
        this.azureFunctionService = azureFunctionService;
    }

    // Método programado para ejecutarse diariamente a las 9:00 AM
    @Scheduled(cron = "0 0 9 * * ?")
    public void verificarRecordatoriosVacunacion() {
        // Implementación de verificación y envío de recordatorios
    }
}
```

## Funciones de Azure Integradas

El microservicio se integra con las siguientes funciones de Azure:

### 1. Función de Notificaciones (for1notificacion)

**URL**: `https://for1notificacion.azurewebsites.net/api/HttpNotificaciones`

Esta función se utiliza para enviar notificaciones a los clientes en diferentes escenarios relacionados con las mascotas y su historial médico.

#### Puntos de Integración con la Función de Notificaciones:

1. **Creación de nuevos registros clínicos**
   - **Cuándo se activa**: Cada vez que se crea un nuevo registro en el historial clínico a través de `POST /api/historial-clinico`
   - **Tipo de notificación**: `HISTORIAL_CREADO`
   - **Contenido**: Información sobre el nuevo registro médico, incluyendo diagnóstico y tratamiento

   **Ejemplo de notificación**:
   ```json
   {
     "idCliente": 1,
     "tipo": "HISTORIAL_CREADO",
     "titulo": "Nuevo registro médico para Rocky",
     "mensaje": "Se ha registrado una nueva consulta médica para Rocky con diagnóstico: Estado saludable, sin patologías evidentes",
     "fechaCreacion": "2025-03-23T14:35:22.123456",
     "canal": "email"
   }
   ```

2. **Recordatorios de vacunación**
   - **Cuándo se activa**: Automáticamente cada día a las 9:00 AM para mascotas que requieren renovación de vacunas (registros de hace aproximadamente un año)
   - **Tipo de notificación**: `RECORDATORIO_VACUNA`
   - **Contenido**: Recordatorio de que es tiempo de renovar la vacunación de la mascota

   **Ejemplo de notificación**:
   ```json
   {
     "idCliente": 2,
     "tipo": "RECORDATORIO_VACUNA",
     "titulo": "Recordatorio de vacunación para Rocky",
     "mensaje": "Es tiempo de renovar la vacunación de Rocky. La última vacunación fue hace aproximadamente un año.",
     "fechaCreacion": "2025-03-23T09:00:00.123456",
     "canal": "email"
   }
   ```

### Configuración

La configuración de las Azure Functions se realiza en el archivo `application.properties`:

```properties
# URLs de Microservicios
microservicio.clientes.url=http://20.62.8.211:8082

# URLs de Funciones Azure
azure.functions.notificaciones.url=https://for1notificacion.azurewebsites.net/api/HttpNotificaciones
azure.functions.laboratorios.url=https://for1laboratorios.azurewebsites.net/api/HttpLaboratorios
```

## API Endpoints (Actualizado)

### Nuevos Endpoints para Recordatorios:

#### Verificar manualmente recordatorios de vacunación
- **URL**: `/api/recordatorios/vacunacion/verificar`
- **Método**: GET
- **Response**: Mensaje de confirmación de ejecución
- **Funcionalidad**: Ejecuta manualmente la verificación de registros médicos para identificar mascotas que requieren renovación de vacunas y envía las notificaciones correspondientes
- **Códigos de Respuesta**:
  - 200: OK con mensaje de confirmación

### Endpoints Modificados de Historial Clínico:

#### Crear nuevo registro clínico (con notificación)
- **URL**: `/api/historial-clinico`
- **Método**: POST
- **Body**: HistorialClinicoCreateDTO
- **Response**: HistorialClinicoDTO
- **Funcionalidad adicional**: Envía una notificación al cliente sobre el nuevo registro médico a través de la función Azure de Notificaciones
- **Códigos de Respuesta**:
  - 201: Creado correctamente
  - 400: Datos inválidos
  - 404: Mascota no encontrada

## Ejemplos de Uso (Formato JSON)

### Ejemplo de creación de registro clínico con notificación (POST /api/historial-clinico)

**Request:**
```json
{
  "idMascota": 1,
  "idCita": 1,
  "idEmpleado": 1,
  "fechaConsulta": "2025-03-23",
  "motivoConsulta": "Vacunación anual",
  "diagnostico": "Mascota en buen estado general, requiere vacuna polivalente",
  "tratamiento": "Vacuna polivalente + desparasitación",
  "observaciones": "Próxima vacuna programada en 1 año. Mascota se mostró tranquila durante el procedimiento."
}
```

**Response (201 Created):**
```json
{
  "idHistorial": 5,
  "idMascota": 1,
  "idCita": 1,
  "idEmpleado": 1,
  "fechaConsulta": "2025-03-23",
  "motivoConsulta": "Vacunación anual",
  "diagnostico": "Mascota en buen estado general, requiere vacuna polivalente",
  "tratamiento": "Vacuna polivalente + desparasitación",
  "observaciones": "Próxima vacuna programada en 1 año. Mascota se mostró tranquila durante el procedimiento."
}
```

**Notificación enviada a Azure Function:**
```json
{
  "idCliente": 2,
  "tipo": "HISTORIAL_CREADO",
  "titulo": "Nuevo registro médico para Rocky",
  "mensaje": "Se ha registrado una nueva consulta médica para Rocky con diagnóstico: Mascota en buen estado general, requiere vacuna polivalente",
  "fechaCreacion": "2025-03-23T14:35:22.123456",
  "canal": "email"
}
```

### Ejemplo de verificación manual de recordatorios de vacunación (GET /api/recordatorios/vacunacion/verificar)

**Response (200 OK):**
```json
{
  "mensaje": "Verificación de recordatorios de vacunación ejecutada correctamente",
  "recordatoriosEnviados": 2
}
```

## Tareas Programadas (Nuevo)

El microservicio implementa las siguientes tareas programadas:

### Verificación diaria de recordatorios de vacunación
- **Cuándo se ejecuta**: Todos los días a las 9:00 AM
- **Función**: `RecordatorioVacunacionService.verificarRecordatoriosVacunacion()`
- **Acción**: Busca registros de historial clínico que contengan vacunaciones realizadas hace aproximadamente un año y envía recordatorios a los clientes a través de la función Azure de Notificaciones

## Consideraciones de Implementación de Azure Functions

1. **Manejo No Bloqueante**: La comunicación con las funciones Azure se realiza de manera no bloqueante. Si la función no responde o devuelve un error, el flujo principal del microservicio continúa sin interrupciones.

2. **Registro de Logs**: Se registran logs detallados de las comunicaciones con las funciones Azure para facilitar la depuración y el monitoreo.

3. **Configuración Centralizada**: Las URLs de las funciones Azure se configuran en el archivo `application.properties`.

4. **Tolerancia a Fallos**: El sistema sigue funcionando incluso si las funciones Azure no están disponibles.

5. **Tareas Programadas**: Se utiliza la anotación `@Scheduled` de Spring para ejecutar tareas periódicas como la verificación de recordatorios de vacunación.

## Pruebas con Postman

### 1. Crear un registro en el historial clínico (que desencadena una notificación)

**Endpoint**: POST /api/historial-clinico
- **URL**: http://localhost:8083/api/historial-clinico
- **Headers**: Content-Type: application/json
- **Body**:
```json
{
  "idMascota": 1,
  "idCita": 3,
  "idEmpleado": 2,
  "fechaConsulta": "2025-03-23",
  "motivoConsulta": "Vacunación anual",
  "diagnostico": "Mascota en buen estado general, requiere vacuna polivalente",
  "tratamiento": "Vacuna polivalente + desparasitación",
  "observaciones": "Próxima vacuna programada en 1 año. Mascota se mostró tranquila durante el procedimiento."
}
```

**Respuesta esperada**: HistorialClinicoDTO (201 Created)

**Funcionamiento interno**: Después de guardar el registro, envía una notificación a la función Azure.

### 2. Verificar manualmente los recordatorios de vacunación

**Endpoint**: GET /api/recordatorios/vacunacion/verificar
- **URL**: http://localhost:8083/api/recordatorios/vacunacion/verificar

**Respuesta esperada**:
```json
{
  "mensaje": "Verificación de recordatorios de vacunación ejecutada correctamente",
  "recordatoriosEnviados": 2
}
```

**Funcionamiento interno**: Busca registros de vacunación de hace aproximadamente un año y envía recordatorios a través de la función Azure.

### 3. Probar directamente la función Azure de Notificaciones

**Endpoint**: POST https://for1notificacion.azurewebsites.net/api/HttpNotificaciones
- **URL**: https://for1notificacion.azurewebsites.net/api/HttpNotificaciones
- **Headers**: Content-Type: application/json
- **Body**:
```json
{
  "idCliente": 1,
  "tipo": "PRUEBA_MANUAL",
  "titulo": "Prueba manual de notificación",
  "mensaje": "Esta es una prueba manual para verificar que la función Azure está recibiendo correctamente las notificaciones.",
  "fechaCreacion": "2025-03-23T16:30:00.000000",
  "canal": "email"
}
```

**Respuesta esperada**: La función actualmente devuelve los mismos datos recibidos como confirmación.