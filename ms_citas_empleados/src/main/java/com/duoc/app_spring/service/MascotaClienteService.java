package com.duoc.app_spring.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MascotaClienteService {

    private final RestTemplate restTemplate;
    
    @Value("${microservicio.mascotas.url:http://20.62.8.211:8083}")
    private String mascotasUrl;
    
    public MascotaClienteService() {
        this.restTemplate = new RestTemplate();
    }
    
    public Long obtenerIdClientePorIdMascota(Long idMascota) {
        try {
            log.info("Obteniendo ID del cliente para mascota ID: {}", idMascota);
            
            String url = mascotasUrl + "/api/mascotas/" + idMascota;
            MascotaDTO mascota = restTemplate.getForObject(url, MascotaDTO.class);
            
            if (mascota != null) {
                log.info("Cliente ID obtenido: {}", mascota.getIdCliente());
                return mascota.getIdCliente();
            }
            
            log.warn("No se pudo obtener la información de la mascota con ID: {}", idMascota);
            // Como estamos en desarrollo, si no podemos obtener el cliente real, devolvemos un ID ficticio para pruebas
            return 1L;
        } catch (Exception e) {
            log.error("Error al obtener la información de la mascota: {}", e.getMessage(), e);
            // Devolvemos un ID ficticio para pruebas
            return 1L;
        }
    }
    
    // Clase interna para mapear la respuesta
    public static class MascotaDTO {
        private Long idMascota;
        private Long idCliente;
        
        // Getters y setters
        public Long getIdMascota() { return idMascota; }
        public void setIdMascota(Long idMascota) { this.idMascota = idMascota; }
        public Long getIdCliente() { return idCliente; }
        public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }
    }
}