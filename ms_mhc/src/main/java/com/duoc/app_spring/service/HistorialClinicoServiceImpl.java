package com.duoc.app_spring.service;

import com.duoc.app_spring.dto.HistorialClinicoCreateDTO;
import com.duoc.app_spring.dto.HistorialClinicoDTO;
import com.duoc.app_spring.dto.NotificacionDTO;
import com.duoc.app_spring.model.HistorialClinico;
import com.duoc.app_spring.model.Mascota;
import com.duoc.app_spring.repository.HistorialClinicoRepository;
import com.duoc.app_spring.repository.MascotaRepository;
import com.duoc.app_spring.util.HistorialClinicoMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HistorialClinicoServiceImpl implements HistorialClinicoService {

    private final HistorialClinicoRepository historialRepository;
    private final MascotaRepository mascotaRepository;
    private final HistorialClinicoMapper historialMapper;
    private final AzureFunctionService azureFunctionService;

    public HistorialClinicoServiceImpl(
            HistorialClinicoRepository historialRepository,
            MascotaRepository mascotaRepository,
            HistorialClinicoMapper historialMapper,
            AzureFunctionService azureFunctionService) {
        this.historialRepository = historialRepository;
        this.mascotaRepository = mascotaRepository;
        this.historialMapper = historialMapper;
        this.azureFunctionService = azureFunctionService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialClinicoDTO> findAll() {
        List<HistorialClinico> registros = historialRepository.findAll();
        return registros.stream()
                .map(historialMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HistorialClinicoDTO findById(Long id) {
        HistorialClinico historial = historialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registro clínico no encontrado con ID: " + id));
        return historialMapper.toDTO(historial);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialClinicoDTO> findByMascota(Long idMascota) {
        if (!mascotaRepository.existsById(idMascota)) {
            throw new EntityNotFoundException("Mascota no encontrada con ID: " + idMascota);
        }
        
        List<HistorialClinico> registros = historialRepository.findByIdMascota(idMascota);
        return registros.stream()
                .map(historialMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialClinicoDTO> findByMascotaOrdenado(Long idMascota) {
        if (!mascotaRepository.existsById(idMascota)) {
            throw new EntityNotFoundException("Mascota no encontrada con ID: " + idMascota);
        }
        
        List<HistorialClinico> registros = historialRepository.findByIdMascotaOrderByFechaConsultaDesc(idMascota);
        return registros.stream()
                .map(historialMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialClinicoDTO> findByEmpleado(Long idEmpleado) {
        List<HistorialClinico> registros = historialRepository.findByIdEmpleado(idEmpleado);
        return registros.stream()
                .map(historialMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialClinicoDTO> findByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<HistorialClinico> registros = historialRepository.findByFechaConsultaBetween(fechaInicio, fechaFin);
        return registros.stream()
                .map(historialMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialClinicoDTO> findByCita(Long idCita) {
        List<HistorialClinico> registros = historialRepository.findByIdCita(idCita);
        return registros.stream()
                .map(historialMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HistorialClinicoDTO create(HistorialClinicoCreateDTO historialCreateDTO) {
        // Verificar que la mascota existe
        Optional<Mascota> mascotaOpt = mascotaRepository.findById(historialCreateDTO.getIdMascota());
        if (mascotaOpt.isEmpty()) {
            throw new EntityNotFoundException("Mascota no encontrada con ID: " + historialCreateDTO.getIdMascota());
        }
        
        Mascota mascota = mascotaOpt.get();
        
        HistorialClinico historial = new HistorialClinico();
        historial.setIdMascota(historialCreateDTO.getIdMascota());
        historial.setIdCita(historialCreateDTO.getIdCita());
        historial.setIdEmpleado(historialCreateDTO.getIdEmpleado());
        historial.setFechaConsulta(historialCreateDTO.getFechaConsulta());
        historial.setMotivoConsulta(historialCreateDTO.getMotivoConsulta());
        historial.setDiagnostico(historialCreateDTO.getDiagnostico());
        historial.setTratamiento(historialCreateDTO.getTratamiento());
        historial.setObservaciones(historialCreateDTO.getObservaciones());
        
        HistorialClinico historialGuardado = historialRepository.save(historial);
        
        // Enviamos notificación al cliente sobre el nuevo registro médico
        enviarNotificacionNuevoRegistroMedico(mascota, historialGuardado);
        
        return historialMapper.toDTO(historialGuardado);
    }

    @Override
    @Transactional
    public HistorialClinicoDTO update(Long id, HistorialClinicoDTO historialDTO) {
        HistorialClinico historialExistente = historialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registro clínico no encontrado con ID: " + id));
        
        // Verificar que la mascota existe si se ha cambiado
        if (!historialExistente.getIdMascota().equals(historialDTO.getIdMascota()) && 
                !mascotaRepository.existsById(historialDTO.getIdMascota())) {
            throw new EntityNotFoundException("Mascota no encontrada con ID: " + historialDTO.getIdMascota());
        }
        
        historialExistente.setIdMascota(historialDTO.getIdMascota());
        historialExistente.setIdCita(historialDTO.getIdCita());
        historialExistente.setIdEmpleado(historialDTO.getIdEmpleado());
        historialExistente.setFechaConsulta(historialDTO.getFechaConsulta());
        historialExistente.setMotivoConsulta(historialDTO.getMotivoConsulta());
        historialExistente.setDiagnostico(historialDTO.getDiagnostico());
        historialExistente.setTratamiento(historialDTO.getTratamiento());
        historialExistente.setObservaciones(historialDTO.getObservaciones());
        
        HistorialClinico historialActualizado = historialRepository.save(historialExistente);
        return historialMapper.toDTO(historialActualizado);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!historialRepository.existsById(id)) {
            throw new EntityNotFoundException("Registro clínico no encontrado con ID: " + id);
        }
        historialRepository.deleteById(id);
    }

    private void enviarNotificacionNuevoRegistroMedico(Mascota mascota, HistorialClinico historial) {
        try {
            NotificacionDTO notificacion = NotificacionDTO.builder()
                    .idCliente(mascota.getIdCliente())
                    .tipo("HISTORIAL_CREADO")
                    .titulo("Nuevo registro médico para " + mascota.getNombre())
                    .mensaje("Se ha registrado una nueva consulta médica para " + mascota.getNombre() + 
                             " con diagnóstico: " + historial.getDiagnostico())
                    .fechaCreacion(LocalDateTime.now())
                    .canal("email")
                    .build();
            
            azureFunctionService.enviarNotificacion(notificacion);
        } catch (Exception e) {
            // Logueamos el error pero no interrumpimos el flujo
            System.err.println("Error al enviar notificación: " + e.getMessage());
        }
    }
}