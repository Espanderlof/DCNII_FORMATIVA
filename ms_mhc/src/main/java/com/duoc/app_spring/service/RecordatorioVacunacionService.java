package com.duoc.app_spring.service;

import com.duoc.app_spring.dto.NotificacionDTO;
import com.duoc.app_spring.model.HistorialClinico;
import com.duoc.app_spring.model.Mascota;
import com.duoc.app_spring.repository.HistorialClinicoRepository;
import com.duoc.app_spring.repository.MascotaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class RecordatorioVacunacionService {

    private final HistorialClinicoRepository historialRepository;
    private final MascotaRepository mascotaRepository;
    private final AzureFunctionService azureFunctionService;

    public RecordatorioVacunacionService(
            HistorialClinicoRepository historialRepository,
            MascotaRepository mascotaRepository,
            AzureFunctionService azureFunctionService) {
        this.historialRepository = historialRepository;
        this.mascotaRepository = mascotaRepository;
        this.azureFunctionService = azureFunctionService;
    }

    // Ejecutar diariamente a las 9:00 AM
    @Scheduled(cron = "0 0 9 * * ?")
    public void verificarRecordatoriosVacunacion() {
        // Buscar registros de historial clínico que tengan tratamientos con la palabra "vacuna"
        // y que hayan sido hace aproximadamente un año (11-13 meses)
        
        LocalDate fechaInicioRango = LocalDate.now().minus(13, ChronoUnit.MONTHS);
        LocalDate fechaFinRango = LocalDate.now().minus(11, ChronoUnit.MONTHS);
        
        List<HistorialClinico> registrosVacunacion = historialRepository.findByFechaConsultaBetweenAndTratamientoContainingIgnoreCase(
                fechaInicioRango, fechaFinRango, "vacuna");
        
        for (HistorialClinico registro : registrosVacunacion) {
            // Para cada registro, verificar que la mascota sigue activa
            Optional<Mascota> mascotaOpt = mascotaRepository.findByIdMascotaAndActivoTrue(registro.getIdMascota());
            
            if (mascotaOpt.isPresent()) {
                Mascota mascota = mascotaOpt.get();
                
                NotificacionDTO notificacion = NotificacionDTO.builder()
                        .idCliente(mascota.getIdCliente())
                        .tipo("RECORDATORIO_VACUNA")
                        .titulo("Recordatorio de vacunación para " + mascota.getNombre())
                        .mensaje("Es tiempo de renovar la vacunación de " + mascota.getNombre() + 
                                 ". La última vacunación fue hace aproximadamente un año.")
                        .fechaCreacion(LocalDateTime.now())
                        .canal("email")
                        .build();
                
                azureFunctionService.enviarNotificacion(notificacion);
            }
        }
    }
}