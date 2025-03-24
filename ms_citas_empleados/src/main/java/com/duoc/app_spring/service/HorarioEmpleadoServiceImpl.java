package com.duoc.app_spring.service;

import com.duoc.app_spring.dto.HorarioEmpleadoDTO;
import com.duoc.app_spring.model.HorarioEmpleado;
import com.duoc.app_spring.repository.EmpleadoRepository;
import com.duoc.app_spring.repository.HorarioEmpleadoRepository;
import com.duoc.app_spring.util.HorarioEmpleadoMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HorarioEmpleadoServiceImpl implements HorarioEmpleadoService {

    private final HorarioEmpleadoRepository horarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final HorarioEmpleadoMapper horarioMapper;

    public HorarioEmpleadoServiceImpl(
            HorarioEmpleadoRepository horarioRepository, 
            EmpleadoRepository empleadoRepository,
            HorarioEmpleadoMapper horarioMapper) {
        this.horarioRepository = horarioRepository;
        this.empleadoRepository = empleadoRepository;
        this.horarioMapper = horarioMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioEmpleadoDTO> findAll() {
        List<HorarioEmpleado> horarios = horarioRepository.findAll();
        return horarios.stream()
                .map(horarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HorarioEmpleadoDTO findById(Long id) {
        HorarioEmpleado horario = horarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Horario no encontrado con ID: " + id));
        return horarioMapper.toDTO(horario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioEmpleadoDTO> findByEmpleado(Long idEmpleado) {
        // Verificar que el empleado existe
        if (!empleadoRepository.existsById(idEmpleado)) {
            throw new EntityNotFoundException("Empleado no encontrado con ID: " + idEmpleado);
        }
        
        List<HorarioEmpleado> horarios = horarioRepository.findByIdEmpleado(idEmpleado);
        return horarios.stream()
                .map(horarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioEmpleadoDTO> findByEmpleadoAndDia(Long idEmpleado, Integer diaSemana) {
        // Verificar que el empleado existe
        if (!empleadoRepository.existsById(idEmpleado)) {
            throw new EntityNotFoundException("Empleado no encontrado con ID: " + idEmpleado);
        }
        
        // Validar que el día de la semana es válido
        if (diaSemana < 1 || diaSemana > 7) {
            throw new IllegalArgumentException("Día de la semana inválido. Debe ser entre 1 y 7.");
        }
        
        List<HorarioEmpleado> horarios = horarioRepository.findByIdEmpleadoAndDiaSemana(idEmpleado, diaSemana);
        return horarios.stream()
                .map(horarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HorarioEmpleadoDTO create(HorarioEmpleadoDTO horarioDTO) {
        // Verificar que el empleado existe
        if (!empleadoRepository.existsById(horarioDTO.getIdEmpleado())) {
            throw new EntityNotFoundException("Empleado no encontrado con ID: " + horarioDTO.getIdEmpleado());
        }
        
        // Validar que el día de la semana es válido
        if (horarioDTO.getDiaSemana() < 1 || horarioDTO.getDiaSemana() > 7) {
            throw new IllegalArgumentException("Día de la semana inválido. Debe ser entre 1 y 7.");
        }
        
        // Validar que la hora de inicio es anterior a la hora de fin
        if (horarioDTO.getHoraInicio().isAfter(horarioDTO.getHoraFin())) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin.");
        }
        
        HorarioEmpleado horario = horarioMapper.toEntity(horarioDTO);
        HorarioEmpleado horarioGuardado = horarioRepository.save(horario);
        return horarioMapper.toDTO(horarioGuardado);
    }

    @Override
    @Transactional
    public HorarioEmpleadoDTO update(Long id, HorarioEmpleadoDTO horarioDTO) {
        // Verificar que el horario existe
        HorarioEmpleado horarioExistente = horarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Horario no encontrado con ID: " + id));
        
        // Verificar que el empleado existe
        if (!empleadoRepository.existsById(horarioDTO.getIdEmpleado())) {
            throw new EntityNotFoundException("Empleado no encontrado con ID: " + horarioDTO.getIdEmpleado());
        }
        
        // Validar que el día de la semana es válido
        if (horarioDTO.getDiaSemana() < 1 || horarioDTO.getDiaSemana() > 7) {
            throw new IllegalArgumentException("Día de la semana inválido. Debe ser entre 1 y 7.");
        }
        
        // Validar que la hora de inicio es anterior a la hora de fin
        if (horarioDTO.getHoraInicio().isAfter(horarioDTO.getHoraFin())) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin.");
        }
        
        horarioExistente.setIdEmpleado(horarioDTO.getIdEmpleado());
        horarioExistente.setDiaSemana(horarioDTO.getDiaSemana());
        horarioExistente.setHoraInicio(horarioDTO.getHoraInicio());
        horarioExistente.setHoraFin(horarioDTO.getHoraFin());
        
        HorarioEmpleado horarioActualizado = horarioRepository.save(horarioExistente);
        return horarioMapper.toDTO(horarioActualizado);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!horarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Horario no encontrado con ID: " + id);
        }
        horarioRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByEmpleado(Long idEmpleado) {
        // Verificar que el empleado existe
        if (!empleadoRepository.existsById(idEmpleado)) {
            throw new EntityNotFoundException("Empleado no encontrado con ID: " + idEmpleado);
        }
        
        horarioRepository.deleteByIdEmpleado(idEmpleado);
    }
}