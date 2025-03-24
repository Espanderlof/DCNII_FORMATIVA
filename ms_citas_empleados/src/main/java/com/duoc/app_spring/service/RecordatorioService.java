package com.duoc.app_spring.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.duoc.app_spring.dto.NotificacionDTO;
import com.duoc.app_spring.model.Cita;
import com.duoc.app_spring.repository.CitaRepository;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class RecordatorioService {

    private final CitaRepository citaRepository;
    private final MascotaClienteService mascotaClienteService;
    private final AzureFunctionService azureFunctionService;
    
    public RecordatorioService(
            CitaRepository citaRepository,
            MascotaClienteService mascotaClienteService,
            AzureFunctionService azureFunctionService) {
        this.citaRepository = citaRepository;
        this.mascotaClienteService = mascotaClienteService;
        this.azureFunctionService = azureFunctionService;
    }
    
    // Ejecutar diariamente a las 8:00 AM
    @Scheduled(cron = "0 0 8 * * ?")
    public void enviarRecordatoriosCitas() {
        log.info("Iniciando envío de recordatorios de citas...");
        
        // Obtener citas programadas para mañana
        LocalDateTime mananaInicio = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime mananaFin = mananaInicio.plusDays(1);
        
        List<Cita> citasManana = citaRepository.findByEstadoAndFechaHoraBetween("programada", mananaInicio, mananaFin);
        
        log.info("Se encontraron {} citas programadas para mañana", citasManana.size());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Cita cita : citasManana) {
            try {
                Long idCliente = mascotaClienteService.obtenerIdClientePorIdMascota(cita.getIdMascota());
                
                NotificacionDTO notificacion = NotificacionDTO.builder()
                        .idCliente(idCliente)
                        .tipo("RECORDATORIO_CITA")
                        .titulo("Recordatorio de cita")
                        .mensaje("Le recordamos que tiene una cita programada para mañana " + 
                                cita.getFechaHora().format(formatter) + ". Motivo: " + cita.getMotivo())
                        .fechaCreacion(LocalDateTime.now())
                        .canal("email")
                        .build();
                        
                azureFunctionService.enviarNotificacion(notificacion);
                log.info("Recordatorio enviado para la cita ID: {}", cita.getIdCita());
            } catch (Exception e) {
                log.error("Error al enviar recordatorio para la cita ID {}: {}", cita.getIdCita(), e.getMessage(), e);
            }
        }
        
        log.info("Proceso de envío de recordatorios finalizado. {} recordatorios enviados.", citasManana.size());
    }
}