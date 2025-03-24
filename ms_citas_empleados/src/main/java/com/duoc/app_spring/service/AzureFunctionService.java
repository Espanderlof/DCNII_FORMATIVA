package com.duoc.app_spring.service;

import com.duoc.app_spring.dto.FacturacionDTO;
import com.duoc.app_spring.dto.NotificacionDTO;
import com.duoc.app_spring.model.Cita;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Service
@Slf4j
public class AzureFunctionService {

    private final RestTemplate restTemplate;
    
    @Value("${azure.functions.notificaciones.url:https://for1notificacion.azurewebsites.net/api/HttpNotificaciones}")
    private String notificacionesUrl;
    
    @Value("${azure.functions.facturacion.url:https://for1facturacion.azurewebsites.net/api/HttpFacturacion}")
    private String facturacionUrl;
    
    @Value("${azure.functions.laboratorios.url:https://for1laboratorios.azurewebsites.net/api/HttpLaboratorios}")
    private String laboratoriosUrl;
    
    public AzureFunctionService() {
        this.restTemplate = new RestTemplate();
    }
    
    public void enviarNotificacion(NotificacionDTO notificacion) {
        try {
            log.info("Enviando notificación: {}", notificacion);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<NotificacionDTO> request = new HttpEntity<>(notificacion, headers);
            
            String response = restTemplate.postForObject(notificacionesUrl, request, String.class);
            log.info("Notificación enviada correctamente: {}", response);
        } catch (Exception e) {
            log.error("Error al enviar la notificación: {}", e.getMessage(), e);
        }
    }
    
    public void generarFactura(Cita cita, Long idCliente) {
        try {
            log.info("Generando factura para cita ID: {}, cliente ID: {}", cita.getIdCita(), idCliente);
            
            FacturacionDTO facturacion = FacturacionDTO.builder()
                .idCita(cita.getIdCita())
                .idCliente(idCliente)
                .idEmpleado(cita.getIdEmpleado())
                .idMascota(cita.getIdMascota())
                .fechaEmision(LocalDateTime.now())
                .tipoCita(cita.getTipoCita())
                .montoEstimado(calcularMontoEstimado(cita.getTipoCita()))
                .build();
                
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<FacturacionDTO> request = new HttpEntity<>(facturacion, headers);
            
            String response = restTemplate.postForObject(facturacionUrl, request, String.class);
            log.info("Solicitud de facturación enviada correctamente: {}", response);
        } catch (Exception e) {
            log.error("Error al enviar la solicitud de facturación: {}", e.getMessage(), e);
        }
    }
    
    // Método simple para calcular un monto estimado según el tipo de cita
    private Double calcularMontoEstimado(String tipoCita) {
        switch (tipoCita.toLowerCase()) {
            case "consulta":
                return 25000.0;
            case "vacunación":
                return 35000.0;
            case "control":
                return 20000.0;
            case "cirugía":
                return 150000.0;
            default:
                return 30000.0;
        }
    }
}