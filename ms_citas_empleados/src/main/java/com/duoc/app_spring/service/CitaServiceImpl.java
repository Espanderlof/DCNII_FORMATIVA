package com.duoc.app_spring.service;

import com.duoc.app_spring.dto.CitaCreateDTO;
import com.duoc.app_spring.dto.CitaDTO;
import com.duoc.app_spring.dto.NotificacionDTO;
import com.duoc.app_spring.model.Cita;
import com.duoc.app_spring.repository.CitaRepository;
import com.duoc.app_spring.repository.EmpleadoRepository;
import com.duoc.app_spring.util.CitaMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final CitaMapper citaMapper;

    private final AzureFunctionService azureFunctionService;
    private final MascotaClienteService mascotaClienteService;

    public CitaServiceImpl(
            CitaRepository citaRepository, 
            EmpleadoRepository empleadoRepository, 
            CitaMapper citaMapper,
            AzureFunctionService azureFunctionService,
            MascotaClienteService mascotaClienteService) {
        this.citaRepository = citaRepository;
        this.empleadoRepository = empleadoRepository;
        this.citaMapper = citaMapper;
        this.azureFunctionService = azureFunctionService;
        this.mascotaClienteService = mascotaClienteService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaDTO> findAll() {
        List<Cita> citas = citaRepository.findAll();
        return citas.stream()
                .map(citaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CitaDTO findById(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada con ID: " + id));
        return citaMapper.toDTO(cita);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaDTO> findByMascota(Long idMascota) {
        List<Cita> citas = citaRepository.findByIdMascota(idMascota);
        return citas.stream()
                .map(citaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaDTO> findByEmpleado(Long idEmpleado) {
        // Verificar que el empleado existe
        if (!empleadoRepository.existsById(idEmpleado)) {
            throw new EntityNotFoundException("Empleado no encontrado con ID: " + idEmpleado);
        }
        
        List<Cita> citas = citaRepository.findByIdEmpleado(idEmpleado);
        return citas.stream()
                .map(citaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaDTO> findByEstado(String estado) {
        // Validar que el estado es válido
        if (!estado.matches("^(programada|completada|cancelada)$")) {
            throw new IllegalArgumentException("Estado inválido. Debe ser 'programada', 'completada' o 'cancelada'.");
        }
        
        List<Cita> citas = citaRepository.findByEstado(estado);
        return citas.stream()
                .map(citaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaDTO> findByRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        // Validar que la fecha de inicio es anterior a la fecha de fin
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }
        
        List<Cita> citas = citaRepository.findByFechaHoraBetween(inicio, fin);
        return citas.stream()
                .map(citaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaDTO> findByEmpleadoAndRangoFechas(Long idEmpleado, LocalDateTime inicio, LocalDateTime fin) {
        // Verificar que el empleado existe
        if (!empleadoRepository.existsById(idEmpleado)) {
            throw new EntityNotFoundException("Empleado no encontrado con ID: " + idEmpleado);
        }
        
        // Validar que la fecha de inicio es anterior a la fecha de fin
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }
        
        List<Cita> citas = citaRepository.findByIdEmpleadoAndFechaHoraBetween(idEmpleado, inicio, fin);
        return citas.stream()
                .map(citaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaDTO> findByMascotaAndEstado(Long idMascota, String estado) {
        // Validar que el estado es válido
        if (!estado.matches("^(programada|completada|cancelada)$")) {
            throw new IllegalArgumentException("Estado inválido. Debe ser 'programada', 'completada' o 'cancelada'.");
        }
        
        List<Cita> citas = citaRepository.findByIdMascotaAndEstado(idMascota, estado);
        return citas.stream()
                .map(citaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CitaDTO create(CitaCreateDTO citaCreateDTO) {
        // Verificar que el empleado existe
        if (!empleadoRepository.existsById(citaCreateDTO.getIdEmpleado())) {
            throw new EntityNotFoundException("Empleado no encontrado con ID: " + citaCreateDTO.getIdEmpleado());
        }
        
        // Verificar disponibilidad del empleado (aquí se podría agregar lógica para verificar que no hay otra cita a la misma hora)
        
        Cita cita = new Cita();
        cita.setIdMascota(citaCreateDTO.getIdMascota());
        cita.setIdEmpleado(citaCreateDTO.getIdEmpleado());
        cita.setFechaHora(citaCreateDTO.getFechaHora());
        cita.setTipoCita(citaCreateDTO.getTipoCita());
        cita.setMotivo(citaCreateDTO.getMotivo());
        cita.setEstado("programada");  // Por defecto, las citas nuevas están programadas
        cita.setNotas(citaCreateDTO.getNotas());
        
        Cita citaGuardada = citaRepository.save(cita);
        
        // Obtener el ID del cliente asociado a la mascota
        Long idCliente = mascotaClienteService.obtenerIdClientePorIdMascota(citaGuardada.getIdMascota());
        
        // Enviar notificación de cita programada
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        NotificacionDTO notificacion = NotificacionDTO.builder()
                .idCliente(idCliente)
                .tipo("CITA_PROGRAMADA")
                .titulo("Nueva cita programada")
                .mensaje("Su cita ha sido programada para el " + citaGuardada.getFechaHora().format(formatter) + 
                        ". Motivo: " + citaGuardada.getMotivo())
                .fechaCreacion(LocalDateTime.now())
                .canal("email") // Se puede configurar según preferencias del cliente
                .build();
                
        azureFunctionService.enviarNotificacion(notificacion);
        
        return citaMapper.toDTO(citaGuardada);
    }

    @Override
    @Transactional
    public CitaDTO update(Long id, CitaDTO citaDTO) {
        Cita citaExistente = citaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada con ID: " + id));
        
        // Verificar que el empleado existe
        if (!empleadoRepository.existsById(citaDTO.getIdEmpleado())) {
            throw new EntityNotFoundException("Empleado no encontrado con ID: " + citaDTO.getIdEmpleado());
        }
        
        // Validar que el estado es válido
        if (!citaDTO.getEstado().matches("^(programada|completada|cancelada)$")) {
            throw new IllegalArgumentException("Estado inválido. Debe ser 'programada', 'completada' o 'cancelada'.");
        }
        
        citaExistente.setIdMascota(citaDTO.getIdMascota());
        citaExistente.setIdEmpleado(citaDTO.getIdEmpleado());
        citaExistente.setFechaHora(citaDTO.getFechaHora());
        citaExistente.setTipoCita(citaDTO.getTipoCita());
        citaExistente.setMotivo(citaDTO.getMotivo());
        citaExistente.setEstado(citaDTO.getEstado());
        citaExistente.setNotas(citaDTO.getNotas());
        
        Cita citaActualizada = citaRepository.save(citaExistente);
        return citaMapper.toDTO(citaActualizada);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!citaRepository.existsById(id)) {
            throw new EntityNotFoundException("Cita no encontrada con ID: " + id);
        }
        citaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CitaDTO completarCita(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada con ID: " + id));
        
        // Solo se pueden completar citas que estén programadas
        if (!"programada".equals(cita.getEstado())) {
            throw new IllegalStateException("Solo se pueden completar citas que estén en estado 'programada'. Estado actual: " + cita.getEstado());
        }
        
        cita.setEstado("completada");
        Cita citaCompletada = citaRepository.save(cita);
        
        // Obtener el ID del cliente asociado a la mascota
        Long idCliente = mascotaClienteService.obtenerIdClientePorIdMascota(citaCompletada.getIdMascota());
        
        // Enviar notificación de cita completada
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        NotificacionDTO notificacion = NotificacionDTO.builder()
                .idCliente(idCliente)
                .tipo("CITA_COMPLETADA")
                .titulo("Cita completada")
                .mensaje("Su cita del " + citaCompletada.getFechaHora().format(formatter) + 
                        " ha sido completada. Gracias por su visita.")
                .fechaCreacion(LocalDateTime.now())
                .canal("email")
                .build();
                
        azureFunctionService.enviarNotificacion(notificacion);
        
        // Llamar a la función de facturación
        azureFunctionService.generarFactura(citaCompletada, idCliente);
        
        return citaMapper.toDTO(citaCompletada);
    }

    @Override
    @Transactional
    public CitaDTO cancelarCita(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada con ID: " + id));
        
        // Solo se pueden cancelar citas que estén programadas
        if (!"programada".equals(cita.getEstado())) {
            throw new IllegalStateException("Solo se pueden cancelar citas que estén en estado 'programada'. Estado actual: " + cita.getEstado());
        }
        
        cita.setEstado("cancelada");
        Cita citaCancelada = citaRepository.save(cita);
        
        // Obtener el ID del cliente asociado a la mascota
        Long idCliente = mascotaClienteService.obtenerIdClientePorIdMascota(citaCancelada.getIdMascota());
        
        // Enviar notificación de cita cancelada
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        NotificacionDTO notificacion = NotificacionDTO.builder()
                .idCliente(idCliente)
                .tipo("CITA_CANCELADA")
                .titulo("Cita cancelada")
                .mensaje("Su cita programada para el " + citaCancelada.getFechaHora().format(formatter) + 
                        " ha sido cancelada.")
                .fechaCreacion(LocalDateTime.now())
                .canal("email")
                .build();
                
        azureFunctionService.enviarNotificacion(notificacion);
        
        return citaMapper.toDTO(citaCancelada);
    }
}