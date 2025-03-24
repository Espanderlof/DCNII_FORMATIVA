package com.duoc.app_spring.service;

import com.duoc.app_spring.dto.NotificacionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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