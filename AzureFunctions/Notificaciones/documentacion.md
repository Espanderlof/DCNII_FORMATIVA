# API REST de Notificaciones

## Descripción
Esta API permite gestionar notificaciones para el sistema de veterinaria.

## Endpoints

### Obtener todas las notificaciones
- **URL**: `/api/notificaciones`
- **Método**: GET
- **Respuesta exitosa**: 
  - Código: 200 OK
  - Contenido: Array de objetos Notificacion

### Obtener notificación por ID
- **URL**: `/api/notificaciones/{id}`
- **Método**: GET
- **Parámetros URL**: 
  - `id`: ID de la notificación
- **Respuesta exitosa**: 
  - Código: 200 OK
  - Contenido: Objeto Notificacion
- **Respuesta de error**:
  - Código: 404 Not Found
  - Contenido: Objeto Error

### Obtener notificaciones por cliente
- **URL**: `/api/notificaciones?idCliente={idCliente}`
- **Método**: GET
- **Parámetros Query**: 
  - `idCliente`: ID del cliente
- **Respuesta exitosa**: 
  - Código: 200 OK
  - Contenido: Array de objetos Notificacion

### Crear notificación
- **URL**: `/api/notificaciones`
- **Método**: POST
- **Cuerpo de la solicitud**: Objeto Notificacion (ver ejemplo abajo)
- **Encabezados requeridos**:
  - `Content-Type: application/json`
- **Ejemplo de solicitud**:
  ```json
  {
    "idCliente": 1,
    "tipo": "CITA_PROGRAMADA",
    "titulo": "Recordatorio de cita",
    "mensaje": "Le recordamos su cita para mañana a las 15:30 con el Dr. García",
    "canal": "EMAIL"
  }
  ```
- **Respuesta exitosa**: 
  - Código: 201 Created
  - Contenido: Objeto Notificacion creado
- **Ejemplo de respuesta exitosa**:
  ```json
  {
    "idNotificacion": 1,
    "idCliente": 1,
    "tipo": "CITA_PROGRAMADA",
    "titulo": "Recordatorio de cita",
    "mensaje": "Le recordamos su cita para mañana a las 15:30 con el Dr. García",
    "fechaCreacion": "2025-04-05T14:30:00",
    "fechaEnvio": null,
    "estado": "PENDIENTE",
    "canal": "EMAIL"
  }
  ```
- **Respuesta de error**:
  - Código: 400 Bad Request
  - Contenido: Objeto Error
- **Notas sobre validación**:
  - El campo `canal` solo puede tener uno de estos valores: "EMAIL", "SMS", "PUSH" (no distingue mayúsculas/minúsculas en la entrada, pero se almacena en mayúsculas)

### Actualizar notificación
- **URL**: `/api/notificaciones/{id}`
- **Método**: PUT
- **Parámetros URL**: 
  - `id`: ID de la notificación a actualizar
- **Cuerpo de la solicitud**: Objeto Notificacion (ver ejemplo abajo)
- **Encabezados requeridos**:
  - `Content-Type: application/json`
- **Ejemplo de solicitud**:
  ```json
  {
    "idCliente": 1,
    "tipo": "CITA_REPROGRAMADA",
    "titulo": "Cambio en su cita",
    "mensaje": "Su cita ha sido reprogramada para el día 12/04/2025 a las 16:00",
    "estado": "PENDIENTE",
    "canal": "EMAIL"
  }
  ```
- **Respuesta exitosa**: 
  - Código: 200 OK
  - Contenido: Objeto Notificacion actualizado
- **Ejemplo de respuesta exitosa**:
  ```json
  {
    "idNotificacion": 1,
    "idCliente": 1,
    "tipo": "CITA_REPROGRAMADA",
    "titulo": "Cambio en su cita",
    "mensaje": "Su cita ha sido reprogramada para el día 12/04/2025 a las 16:00",
    "fechaCreacion": "2025-04-05T14:30:00",
    "fechaEnvio": null,
    "estado": "PENDIENTE",
    "canal": "EMAIL"
  }
  ```
- **Respuesta de error**:
  - Código: 404 Not Found o 400 Bad Request
  - Contenido: Objeto Error
- **Notas**:
  - El ID de la notificación debe especificarse en la URL, no en el cuerpo de la solicitud.
  - Todos los campos requeridos deben incluirse en el cuerpo de la solicitud, incluso si solo se va a actualizar un campo específico.

### Eliminar notificación
- **URL**: `/api/notificaciones/{id}`
- **Método**: DELETE
- **Parámetros URL**: 
  - `id`: ID de la notificación a eliminar
- **Respuesta exitosa**: 
  - Código: 204 No Content
- **Respuesta de error**:
  - Código: 404 Not Found
  - Contenido: Objeto Error

## Modelo de datos

### Notificacion
```json
{
  "idNotificacion": 1,
  "idCliente": 100,
  "tipo": "CITA_PROGRAMADA",
  "titulo": "Confirmación de cita",
  "mensaje": "Su cita ha sido programada para el 10/04/2025 a las 15:30",
  "fechaCreacion": "2025-03-23T14:30:00",
  "fechaEnvio": "2025-03-23T14:31:00",
  "estado": "ENVIADO",
  "canal": "EMAIL"
}
```

### Campos requeridos para creación
- `idCliente`: ID del cliente al que se dirige la notificación
- `tipo`: Tipo de notificación (por ejemplo, "CITA_PROGRAMADA", "CITA_REPROGRAMADA", "RECORDATORIO", etc.)
- `titulo`: Título breve de la notificación
- `mensaje`: Contenido completo de la notificación
- `canal`: Canal de envío - uno de: "EMAIL", "SMS", "PUSH"

### Error
```json
{
  "error": "Notificación no encontrada",
  "mensaje": "No existe notificación con ID: 999"
}
```