package com.duoc.app_spring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.duoc.app_spring.dto.MascotaDTO;

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
            // Primero obtenemos la mascota para saber su idCliente
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