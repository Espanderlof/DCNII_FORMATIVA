# Documentación del Microservicio de Clientes

## Descripción General

El Microservicio de Clientes forma parte del sistema de veterinaria CloudNative y es responsable de gestionar toda la información relacionada con los clientes de la veterinaria. Implementa operaciones CRUD (Crear, Leer, Actualizar, Eliminar) para entidades Cliente y proporciona endpoints RESTful para su integración con el frontend Angular y otros microservicios del sistema.

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
│   └── ClienteController.java
├── dto
│   ├── ClienteCreateDTO.java
│   ├── ClienteDTO.java
│   └── NotificacionDTO.java
├── model
│   └── Cliente.java
├── repository
│   └── ClienteRepository.java
├── service
│   ├── AzureFunctionService.java
│   ├── ClienteService.java
│   └── ClienteServiceImpl.java
├── util
│   ├── ClienteMapper.java
│   └── GlobalExceptionHandler.java
├── App.java
```

## Modelo de Datos

La entidad Cliente representa a un cliente de la veterinaria y contiene los siguientes campos:

| Campo         | Tipo      | Descripción                                   |
|---------------|-----------|-----------------------------------------------|
| idCliente     | Long      | Identificador único del cliente (PK)          |
| rut           | String    | RUT del cliente (UK)                          |
| nombres       | String    | Nombres del cliente                           |
| apellidos     | String    | Apellidos del cliente                         |
| email         | String    | Correo electrónico del cliente                |
| telefono      | String    | Teléfono de contacto                          |
| direccion     | String    | Dirección del cliente                         |
| ciudad        | String    | Ciudad de residencia                          |
| fechaRegistro | LocalDate | Fecha de registro en el sistema               |
| activo        | Boolean   | Indica si el cliente está activo (true) o no (false) |

## DTOs (Data Transfer Objects)

El microservicio utiliza los siguientes DTOs:

### ClienteDTO
Representa un cliente completo con todos sus atributos, usado tanto para respuestas como para actualizaciones.

### ClienteCreateDTO
Usado exclusivamente para la creación de nuevos clientes, contiene solo los campos necesarios para la creación.

### NotificacionDTO
Utilizado para enviar notificaciones a través de la función Azure de Notificaciones.

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDTO {
    private Long idCliente;
    private String tipo;  // CLIENTE_CREADO, CLIENTE_ACTUALIZADO, CLIENTE_DESACTIVADO
    private String titulo;
    private String mensaje;
    private LocalDateTime fechaCreacion;
    private String canal; // email, SMS, push
}
```

## Integración con Azure Functions

El microservicio se integra con la función Azure de Notificaciones para enviar mensajes a los clientes en diferentes eventos relacionados con su cuenta.

### AzureFunctionService

Este servicio se encarga de la comunicación con las funciones de Azure utilizando REST.

```java
@Service
public class AzureFunctionService {
    private static final Logger log = LoggerFactory.getLogger(AzureFunctionService.class);
    
    private final RestTemplate restTemplate;
    
    @Value("${azure.functions.notificaciones.url}")
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

### Configuración

La URL de la función Azure se configura en el archivo `application.properties`:

```properties
# URLs de Funciones Azure
azure.functions.notificaciones.url=https://for1notificacion.azurewebsites.net/api/HttpNotificaciones
```

### Puntos de Integración

El microservicio utiliza la función Azure de Notificaciones en los siguientes eventos del ciclo de vida del cliente:

1. **Creación de Cliente**
   - Cuando se crea un cliente nuevo, se envía una notificación de bienvenida.
   - Tipo de notificación: `CLIENTE_CREADO`

2. **Actualización de Cliente**
   - Cuando se actualiza la información de un cliente, se envía una notificación informativa.
   - Tipo de notificación: `CLIENTE_ACTUALIZADO`

3. **Desactivación de Cliente**
   - Cuando se desactiva una cuenta de cliente, se envía una notificación explicativa.
   - Tipo de notificación: `CLIENTE_DESACTIVADO`

## API Endpoints

### Obtener todos los clientes
- **URL**: `/api/clientes`
- **Método**: GET
- **Response**: Lista de ClienteDTO
- **Códigos de Respuesta**:
  - 200: OK

### Obtener cliente por ID
- **URL**: `/api/clientes/{id}`
- **Método**: GET
- **Parámetros**:
  - `id`: ID del cliente a consultar
- **Response**: ClienteDTO
- **Códigos de Respuesta**:
  - 200: OK
  - 404: Cliente no encontrado

### Obtener cliente por RUT
- **URL**: `/api/clientes/rut/{rut}`
- **Método**: GET
- **Parámetros**:
  - `rut`: RUT del cliente a consultar
- **Response**: ClienteDTO
- **Códigos de Respuesta**:
  - 200: OK
  - 404: Cliente no encontrado

### Crear nuevo cliente
- **URL**: `/api/clientes`
- **Método**: POST
- **Body**: ClienteCreateDTO
- **Response**: ClienteDTO
- **Funcionalidad adicional**: Envía una notificación de bienvenida al cliente a través de la función Azure de Notificaciones.
- **Códigos de Respuesta**:
  - 201: Creado correctamente
  - 400: Datos inválidos o RUT duplicado

### Actualizar cliente existente
- **URL**: `/api/clientes/{id}`
- **Método**: PUT
- **Parámetros**:
  - `id`: ID del cliente a actualizar
- **Body**: ClienteDTO
- **Response**: ClienteDTO actualizado
- **Funcionalidad adicional**: Envía una notificación al cliente sobre la actualización de su información a través de la función Azure de Notificaciones.
- **Códigos de Respuesta**:
  - 200: OK
  - 400: Datos inválidos o RUT duplicado
  - 404: Cliente no encontrado

### Eliminar cliente
- **URL**: `/api/clientes/{id}`
- **Método**: DELETE
- **Parámetros**:
  - `id`: ID del cliente a eliminar
- **Códigos de Respuesta**:
  - 204: Eliminado correctamente
  - 404: Cliente no encontrado

### Activar cliente
- **URL**: `/api/clientes/{id}/activar`
- **Método**: PATCH
- **Parámetros**:
  - `id`: ID del cliente a activar
- **Response**: ClienteDTO con estado activo
- **Códigos de Respuesta**:
  - 200: OK
  - 404: Cliente no encontrado

### Desactivar cliente
- **URL**: `/api/clientes/{id}/desactivar`
- **Método**: PATCH
- **Parámetros**:
  - `id`: ID del cliente a desactivar
- **Response**: ClienteDTO con estado inactivo
- **Funcionalidad adicional**: Envía una notificación al cliente sobre la desactivación de su cuenta a través de la función Azure de Notificaciones.
- **Códigos de Respuesta**:
  - 200: OK
  - 404: Cliente no encontrado

## Ejemplos de Uso (Formato JSON)

### Ejemplo de creación de cliente (POST /api/clientes)

**Request:**
```json
{
  "rut": "12.345.678-9",
  "nombres": "Juan Carlos",
  "apellidos": "Pérez González",
  "email": "juan.perez@ejemplo.com",
  "telefono": "+56912345678",
  "direccion": "Av. Providencia 1234, Depto 56",
  "ciudad": "Santiago"
}
```

**Response (201 Created):**
```json
{
  "idCliente": 1,
  "rut": "12.345.678-9",
  "nombres": "Juan Carlos",
  "apellidos": "Pérez González",
  "email": "juan.perez@ejemplo.com",
  "telefono": "+56912345678",
  "direccion": "Av. Providencia 1234, Depto 56",
  "ciudad": "Santiago",
  "fechaRegistro": "2025-03-23",
  "activo": true
}
```

**Notificación enviada a Azure Function:**
```json
{
  "idCliente": 1,
  "tipo": "CLIENTE_CREADO",
  "titulo": "Bienvenido a Veterinaria CloudNative",
  "mensaje": "Estimado/a Juan Carlos, ¡bienvenido a nuestra veterinaria! Su cuenta ha sido creada exitosamente.",
  "fechaCreacion": "2025-03-23T15:45:30.123",
  "canal": "email"
}
```

### Ejemplo de actualización de cliente (PUT /api/clientes/1)

**Request:**
```json
{
  "idCliente": 1,
  "rut": "12.345.678-9",
  "nombres": "Juan Carlos",
  "apellidos": "Pérez Soto",
  "email": "juancarlos.perez@ejemplo.com",
  "telefono": "+56912345678",
  "direccion": "Av. Las Condes 5678, Depto 123",
  "ciudad": "Santiago",
  "fechaRegistro": "2025-03-23",
  "activo": true
}
```

**Response (200 OK):**
```json
{
  "idCliente": 1,
  "rut": "12.345.678-9",
  "nombres": "Juan Carlos",
  "apellidos": "Pérez Soto",
  "email": "juancarlos.perez@ejemplo.com",
  "telefono": "+56912345678",
  "direccion": "Av. Las Condes 5678, Depto 123",
  "ciudad": "Santiago",
  "fechaRegistro": "2025-03-23",
  "activo": true
}
```

**Notificación enviada a Azure Function:**
```json
{
  "idCliente": 1,
  "tipo": "CLIENTE_ACTUALIZADO",
  "titulo": "Información actualizada",
  "mensaje": "Estimado/a Juan Carlos, su información ha sido actualizada correctamente.",
  "fechaCreacion": "2025-03-23T16:10:45.789",
  "canal": "email"
}
```

### Ejemplo de desactivación de cliente (PATCH /api/clientes/1/desactivar)

**Response (200 OK):**
```json
{
  "idCliente": 1,
  "rut": "12.345.678-9",
  "nombres": "Juan Carlos",
  "apellidos": "Pérez Soto",
  "email": "juancarlos.perez@ejemplo.com",
  "telefono": "+56912345678",
  "direccion": "Av. Las Condes 5678, Depto 123",
  "ciudad": "Santiago",
  "fechaRegistro": "2025-03-23",
  "activo": false
}
```

**Notificación enviada a Azure Function:**
```json
{
  "idCliente": 1,
  "tipo": "CLIENTE_DESACTIVADO",
  "titulo": "Cuenta desactivada",
  "mensaje": "Estimado/a Juan Carlos, su cuenta ha sido desactivada. Si desea reactivarla, por favor contáctenos.",
  "fechaCreacion": "2025-03-23T16:30:22.456",
  "canal": "email"
}
```

## Manejo de Errores

El microservicio implementa un manejo centralizado de excepciones a través de la clase `GlobalExceptionHandler`. Los principales errores manejados son:

### Entidad No Encontrada (404 Not Found)
```json
{
  "status": 404,
  "message": "Cliente no encontrado con ID: 999",
  "timestamp": "2025-03-23T17:30:45.123"
}
```

### Argumento Inválido (400 Bad Request)
```json
{
  "status": 400,
  "message": "Ya existe un cliente con el RUT: 12.345.678-9",
  "timestamp": "2025-03-23T17:31:12.456"
}
```

### Error de Validación (400 Bad Request)
```json
{
  "rut": "Formato de RUT inválido. Debe ser como 12.345.678-9",
  "nombres": "El nombre es obligatorio",
  "email": "Formato de email inválido"
}
```

## Validaciones

El microservicio implementa las siguientes validaciones para los datos de entrada:

- **RUT**: Debe seguir el formato chileno (XX.XXX.XXX-X) y ser único en el sistema
- **Nombres y Apellidos**: No pueden estar vacíos y deben tener entre 2 y 100 caracteres
- **Email**: Debe ser una dirección de correo válida
- **Teléfono**: Debe seguir un formato válido (+569XXXXXXXX)
- **Dirección**: Máximo 200 caracteres
- **Ciudad**: Máximo 50 caracteres

## Consideraciones de Implementación de Azure Functions

1. **Manejo No Bloqueante**: La comunicación con las funciones Azure se realiza de manera no bloqueante. Si la función no responde o devuelve un error, el flujo principal del microservicio continúa sin interrupciones.

2. **Registro de Logs**: Se registran logs detallados de las comunicaciones con las funciones Azure para facilitar la depuración y el monitoreo.

3. **Configuración Centralizada**: Las URLs de las funciones Azure se configuran en el archivo `application.properties`.

4. **Tolerancia a Fallos**: El sistema sigue funcionando incluso si las funciones Azure no están disponibles.

## Seguridad

Este microservicio actualmente no implementa autenticación ni autorización. La seguridad será manejada a nivel de API Gateway y/o Azure Active Directory según la arquitectura del sistema.

## Consideraciones de Implementación

1. **Soft Delete**: Se utiliza el campo `activo` para implementar un borrado lógico, permitiendo mantener los datos históricos.
2. **Mapeo DTO-Entidad**: Se utiliza un mapper personalizado (`ClienteMapper`) para convertir entre entidades y DTOs.
3. **Validación**: Se utilizan anotaciones de Jakarta Validation para validar datos de entrada.
4. **CORS**: Se ha configurado para permitir solicitudes desde el frontend Angular.
5. **Integración con Azure Functions**: Se utiliza RestTemplate para comunicarse con las funciones de Azure.

## Integración con Otros Microservicios

Este microservicio será consumido principalmente por:

- Frontend Angular (a través del BFF)
- Microservicio de Mascotas (para relacionar mascotas con sus dueños)
- Microservicio de Citas (para agendar citas a clientes)
- Función Azure de Notificaciones (para enviar notificaciones a los clientes)
- Función Azure de Facturación (para generar facturas a clientes)