package com.duoc.app_spring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para la comunicación con las Azure Functions
 */
@Service
public class AzureFunctionService {
    
    private static final Logger log = LoggerFactory.getLogger(AzureFunctionService.class);
    
    private final RestTemplate restTemplate;
    
    @Value("${azure.functions.notificaciones.url:https://for1notificacion.azurewebsites.net/api/HttpNotificaciones}")
    private String notificacionesUrl;
    
    public AzureFunctionService() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Envía una notificación de stock bajo para un producto
     * @param idProducto ID del producto
     * @param nombreProducto Nombre del producto
     * @param stockActual Stock actual
     * @param stockMinimo Stock mínimo
     */
    public void enviarAlertaStockBajo(Long idProducto, String nombreProducto, Integer stockActual, Integer stockMinimo) {
        try {
            // Verificamos que todos los parámetros sean válidos
            if (idProducto == null) {
                log.warn("No se puede enviar alerta: ID de producto nulo");
                return;
            }
            
            if (nombreProducto == null) {
                nombreProducto = "Producto #" + idProducto;
                log.warn("Nombre de producto nulo, usando: {}", nombreProducto);
            }
            
            if (stockActual == null) {
                stockActual = 0;
                log.warn("Stock actual nulo, usando: {}", stockActual);
            }
            
            if (stockMinimo == null) {
                stockMinimo = 0;
                log.warn("Stock mínimo nulo, usando: {}", stockMinimo);
            }
            
            Map<String, Object> notificacion = new HashMap<>();
            notificacion.put("tipo", "STOCK_BAJO");
            notificacion.put("titulo", "Alerta de Stock Bajo");
            notificacion.put("mensaje", String.format("El producto '%s' (ID: %d) tiene un stock bajo: %d unidades (mínimo: %d).",
                    nombreProducto, idProducto, stockActual, stockMinimo));
            notificacion.put("fechaCreacion", LocalDateTime.now().toString());
            
            log.info("Enviando alerta de stock bajo a la función Azure: {}", notificacion);
            
            // Si estamos en ambiente de desarrollo, simulamos la respuesta
            if (notificacionesUrl == null || notificacionesUrl.isEmpty() || notificacionesUrl.equals("${azure.functions.notificaciones.url}")) {
                log.warn("URL de notificaciones no configurada o inválida. Ejecutando en modo simulación.");
                log.info("Simulando respuesta de la función Azure: {}", notificacion);
            } else {
                ResponseEntity<String> response = restTemplate.postForEntity(notificacionesUrl, notificacion, String.class);
                log.info("Respuesta de la función Azure: {}", response.getBody());
            }
        } catch (Exception e) {
            log.error("Error al enviar alerta de stock bajo a la función Azure: {}", e.getMessage(), e);
            // No lanzamos excepción para no interrumpir el flujo principal
        }
    }
    
    /**
     * Envía una notificación de producto por vencer
     * @param idProducto ID del producto
     * @param nombreProducto Nombre del producto
     * @param fechaVencimiento Fecha de vencimiento del producto
     * @param diasRestantes Días restantes hasta el vencimiento
     */
    public void enviarAlertaProductoPorVencer(Long idProducto, String nombreProducto, String fechaVencimiento, long diasRestantes) {
        try {
            // Verificamos que todos los parámetros sean válidos
            if (idProducto == null) {
                log.warn("No se puede enviar alerta: ID de producto nulo");
                return;
            }
            
            if (nombreProducto == null) {
                nombreProducto = "Producto #" + idProducto;
                log.warn("Nombre de producto nulo, usando: {}", nombreProducto);
            }
            
            if (fechaVencimiento == null) {
                fechaVencimiento = "fecha desconocida";
                log.warn("Fecha de vencimiento nula, usando: {}", fechaVencimiento);
            }
            
            Map<String, Object> notificacion = new HashMap<>();
            notificacion.put("tipo", "PRODUCTO_POR_VENCER");
            notificacion.put("titulo", "Alerta de Producto por Vencer");
            notificacion.put("mensaje", String.format("El producto '%s' (ID: %d) está próximo a vencer: %s (faltan %d días).",
                    nombreProducto, idProducto, fechaVencimiento, diasRestantes));
            notificacion.put("fechaCreacion", LocalDateTime.now().toString());
            
            log.info("Enviando alerta de producto por vencer a la función Azure: {}", notificacion);
            
            // Si estamos en ambiente de desarrollo, simulamos la respuesta
            if (notificacionesUrl == null || notificacionesUrl.isEmpty() || notificacionesUrl.equals("${azure.functions.notificaciones.url}")) {
                log.warn("URL de notificaciones no configurada o inválida. Ejecutando en modo simulación.");
                log.info("Simulando respuesta de la función Azure: {}", notificacion);
            } else {
                ResponseEntity<String> response = restTemplate.postForEntity(notificacionesUrl, notificacion, String.class);
                log.info("Respuesta de la función Azure: {}", response.getBody());
            }
        } catch (Exception e) {
            log.error("Error al enviar alerta de producto por vencer a la función Azure: {}", e.getMessage(), e);
            // No lanzamos excepción para no interrumpir el flujo principal
        }
    }
}