package com.function.function;

import com.function.dao.NotificacionDAO;
import com.function.model.Notificacion;
import com.function.util.GsonConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

import java.sql.SQLException;
import java.util.*;

public class NotificacionFunction {
    private final Gson gson = GsonConfig.getGson();
    private final NotificacionDAO notificacionDAO = new NotificacionDAO();

    @FunctionName("HttpNotificaciones")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", 
                        methods = {HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE}, 
                        authLevel = AuthorizationLevel.ANONYMOUS,
                        route = "notificaciones/{id=none}")
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {
        
        context.getLogger().info("Procesando solicitud HTTP para Notificaciones");
        
        try {
            // Determinar la acción según el método HTTP
            HttpMethod method = request.getHttpMethod();
            
            if (method == HttpMethod.GET) {
                return handleGet(request, id);
            } else if (method == HttpMethod.POST) {
                return handlePost(request);
            } else if (method == HttpMethod.PUT) {
                return handlePut(request, id);
            } else if (method == HttpMethod.DELETE) {
                return handleDelete(request, id);
            } else {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("Método no soportado")
                        .build();
            }
        } catch (SQLException e) {
            context.getLogger().severe("Error de base de datos: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error de base de datos", e.getMessage()))
                    .build();
        } catch (Exception e) {
            context.getLogger().severe("Error no esperado: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error interno", e.getMessage()))
                    .build();
        }
    }

    /**
     * Maneja peticiones GET para obtener notificaciones
     */
    private HttpResponseMessage handleGet(HttpRequestMessage<Optional<String>> request, String idParam) throws SQLException {
        // Verificar si se está solicitando por ID o por idCliente o todos
        String clienteParam = request.getQueryParameters().get("idCliente");
        
        if (idParam != null && !idParam.equals("none")) {
            // Buscar por ID
            try {
                Long id = Long.parseLong(idParam);
                Optional<Notificacion> notificacion = notificacionDAO.findById(id);
                
                if (notificacion.isPresent()) {
                    return request.createResponseBuilder(HttpStatus.OK)
                            .header("Content-Type", "application/json")
                            .body(gson.toJson(notificacion.get()))
                            .build();
                } else {
                    return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                            .body(createErrorResponse("Notificación no encontrada", "No existe notificación con ID: " + id))
                            .build();
                }
            } catch (NumberFormatException e) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("ID inválido", "El ID debe ser un número entero"))
                        .build();
            }
        } else if (clienteParam != null && !clienteParam.isEmpty()) {
            // Buscar por cliente
            try {
                Long idCliente = Long.parseLong(clienteParam);
                List<Notificacion> notificaciones = notificacionDAO.findByCliente(idCliente);
                
                return request.createResponseBuilder(HttpStatus.OK)
                        .header("Content-Type", "application/json")
                        .body(gson.toJson(notificaciones))
                        .build();
            } catch (NumberFormatException e) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("ID de cliente inválido", "El ID de cliente debe ser un número entero"))
                        .build();
            }
        } else {
            // Obtener todas
            List<Notificacion> notificaciones = notificacionDAO.findAll();
            
            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(gson.toJson(notificaciones))
                    .build();
        }
    }

    /**
     * Maneja peticiones POST para crear notificaciones
     */
    private HttpResponseMessage handlePost(HttpRequestMessage<Optional<String>> request) throws SQLException {
        String requestBody = request.getBody().orElse("");
        
        if (requestBody.isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Datos inválidos", "El cuerpo de la solicitud está vacío"))
                    .build();
        }
        
        try {
            Notificacion notificacion = gson.fromJson(requestBody, Notificacion.class);
            validateNotificacion(notificacion);
            
            Notificacion savedNotificacion = notificacionDAO.save(notificacion);
            
            return request.createResponseBuilder(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(gson.toJson(savedNotificacion))
                    .build();
        } catch (JsonSyntaxException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Formato inválido", "El formato JSON es incorrecto"))
                    .build();
        } catch (IllegalArgumentException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Datos inválidos", e.getMessage()))
                    .build();
        }
    }

    /**
     * Maneja peticiones PUT para actualizar notificaciones
     */
    private HttpResponseMessage handlePut(HttpRequestMessage<Optional<String>> request, String idParam) throws SQLException {
        if (idParam == null || idParam.equals("none")) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("ID inválido", "Se requiere un ID para actualizar"))
                    .build();
        }
        
        String requestBody = request.getBody().orElse("");
        
        if (requestBody.isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Datos inválidos", "El cuerpo de la solicitud está vacío"))
                    .build();
        }
        
        try {
            Long id = Long.parseLong(idParam);
            
            // Verificar si existe la notificación
            Optional<Notificacion> existingNotificacion = notificacionDAO.findById(id);
            if (!existingNotificacion.isPresent()) {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Notificación no encontrada", "No existe notificación con ID: " + id))
                        .build();
            }
            
            Notificacion notificacion = gson.fromJson(requestBody, Notificacion.class);
            notificacion.setIdNotificacion(id); // Asegurar que el ID es el correcto
            validateNotificacion(notificacion);
            
            Notificacion updatedNotificacion = notificacionDAO.save(notificacion);
            
            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(gson.toJson(updatedNotificacion))
                    .build();
        } catch (NumberFormatException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("ID inválido", "El ID debe ser un número entero"))
                    .build();
        } catch (JsonSyntaxException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Formato inválido", "El formato JSON es incorrecto"))
                    .build();
        } catch (IllegalArgumentException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Datos inválidos", e.getMessage()))
                    .build();
        }
    }

    /**
     * Maneja peticiones DELETE para eliminar notificaciones
     */
    private HttpResponseMessage handleDelete(HttpRequestMessage<Optional<String>> request, String idParam) throws SQLException {
        if (idParam == null || idParam.equals("none")) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("ID inválido", "Se requiere un ID para eliminar"))
                    .build();
        }
        
        try {
            Long id = Long.parseLong(idParam);
            
            // Verificar si existe la notificación
            Optional<Notificacion> existingNotificacion = notificacionDAO.findById(id);
            if (!existingNotificacion.isPresent()) {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Notificación no encontrada", "No existe notificación con ID: " + id))
                        .build();
            }
            
            boolean deleted = notificacionDAO.delete(id);
            
            if (deleted) {
                return request.createResponseBuilder(HttpStatus.NO_CONTENT)
                        .build();
            } else {
                return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createErrorResponse("Error al eliminar", "No se pudo eliminar la notificación"))
                        .build();
            }
        } catch (NumberFormatException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("ID inválido", "El ID debe ser un número entero"))
                    .build();
        }
    }

    /**
     * Valida los datos de una notificación
     */
    private void validateNotificacion(Notificacion notificacion) {
        if (notificacion.getIdCliente() == null) {
            throw new IllegalArgumentException("El ID de cliente es obligatorio");
        }
        
        if (notificacion.getTipo() == null || notificacion.getTipo().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de notificación es obligatorio");
        }
        
        if (notificacion.getTitulo() == null || notificacion.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título es obligatorio");
        }
        
        if (notificacion.getMensaje() == null || notificacion.getMensaje().trim().isEmpty()) {
            throw new IllegalArgumentException("El mensaje es obligatorio");
        }
        
        if (notificacion.getCanal() == null || notificacion.getCanal().trim().isEmpty()) {
            throw new IllegalArgumentException("El canal es obligatorio");
        }
        
        // Validar que el canal sea uno de los valores permitidos
        String canal = notificacion.getCanal().toUpperCase();
        if (!canal.equals("EMAIL") && !canal.equals("SMS") && !canal.equals("PUSH")) {
            throw new IllegalArgumentException("El canal debe ser uno de los siguientes valores: EMAIL, SMS, PUSH");
        }
        
        // Opcionalmente, normalizamos el valor a mayúsculas
        notificacion.setCanal(canal);
    }

    /**
     * Crea una respuesta de error en formato JSON
     */
    private String createErrorResponse(String error, String mensaje) {
        JsonObject response = new JsonObject();
        response.addProperty("error", error);
        response.addProperty("mensaje", mensaje);
        return gson.toJson(response);
    }
}