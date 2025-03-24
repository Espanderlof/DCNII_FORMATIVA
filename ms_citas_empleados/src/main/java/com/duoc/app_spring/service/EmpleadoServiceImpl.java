package com.duoc.app_spring.service;

import com.duoc.app_spring.dto.EmpleadoCreateDTO;
import com.duoc.app_spring.dto.EmpleadoDTO;
import com.duoc.app_spring.model.Empleado;
import com.duoc.app_spring.repository.EmpleadoRepository;
import com.duoc.app_spring.util.EmpleadoMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoMapper empleadoMapper;

    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository, EmpleadoMapper empleadoMapper) {
        this.empleadoRepository = empleadoRepository;
        this.empleadoMapper = empleadoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoDTO> findAll() {
        List<Empleado> empleados = empleadoRepository.findAll();
        return empleados.stream()
                .map(empleadoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmpleadoDTO findById(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con ID: " + id));
        return empleadoMapper.toDTO(empleado);
    }

    @Override
    @Transactional(readOnly = true)
    public EmpleadoDTO findByRut(String rut) {
        Empleado empleado = empleadoRepository.findByRut(rut)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con RUT: " + rut));
        return empleadoMapper.toDTO(empleado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoDTO> findByCargo(String cargo) {
        List<Empleado> empleados = empleadoRepository.findByCargo(cargo);
        return empleados.stream()
                .map(empleadoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoDTO> findByEspecialidad(String especialidad) {
        List<Empleado> empleados = empleadoRepository.findByEspecialidad(especialidad);
        return empleados.stream()
                .map(empleadoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoDTO> findActivos() {
        List<Empleado> empleados = empleadoRepository.findByActivoTrue();
        return empleados.stream()
                .map(empleadoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmpleadoDTO create(EmpleadoCreateDTO empleadoCreateDTO) {
        // Verificar si ya existe un empleado con el mismo RUT
        if (empleadoRepository.existsByRut(empleadoCreateDTO.getRut())) {
            throw new IllegalArgumentException("Ya existe un empleado con el RUT: " + empleadoCreateDTO.getRut());
        }
        
        // Verificar si ya existe un empleado con el mismo email
        if (empleadoRepository.existsByEmail(empleadoCreateDTO.getEmail())) {
            throw new IllegalArgumentException("Ya existe un empleado con el email: " + empleadoCreateDTO.getEmail());
        }
        
        // Crear un nuevo empleado
        Empleado empleado = new Empleado();
        empleado.setRut(empleadoCreateDTO.getRut());
        empleado.setNombres(empleadoCreateDTO.getNombres());
        empleado.setApellidos(empleadoCreateDTO.getApellidos());
        empleado.setCargo(empleadoCreateDTO.getCargo());
        empleado.setEspecialidad(empleadoCreateDTO.getEspecialidad());
        empleado.setEmail(empleadoCreateDTO.getEmail());
        empleado.setTelefono(empleadoCreateDTO.getTelefono());
        empleado.setFechaContratacion(empleadoCreateDTO.getFechaContratacion());
        empleado.setActivo(true);
        
        Empleado empleadoGuardado = empleadoRepository.save(empleado);
        return empleadoMapper.toDTO(empleadoGuardado);
    }

    @Override
    @Transactional
    public EmpleadoDTO update(Long id, EmpleadoDTO empleadoDTO) {
        Empleado empleadoExistente = empleadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con ID: " + id));
        
        // Si se está cambiando el RUT, verificar que no exista otro empleado con ese RUT
        if (!empleadoExistente.getRut().equals(empleadoDTO.getRut()) && 
                empleadoRepository.existsByRut(empleadoDTO.getRut())) {
            throw new IllegalArgumentException("Ya existe un empleado con el RUT: " + empleadoDTO.getRut());
        }
        
        // Si se está cambiando el email, verificar que no exista otro empleado con ese email
        if (!empleadoExistente.getEmail().equals(empleadoDTO.getEmail()) && 
                empleadoRepository.existsByEmail(empleadoDTO.getEmail())) {
            throw new IllegalArgumentException("Ya existe un empleado con el email: " + empleadoDTO.getEmail());
        }
        
        empleadoExistente.setRut(empleadoDTO.getRut());
        empleadoExistente.setNombres(empleadoDTO.getNombres());
        empleadoExistente.setApellidos(empleadoDTO.getApellidos());
        empleadoExistente.setCargo(empleadoDTO.getCargo());
        empleadoExistente.setEspecialidad(empleadoDTO.getEspecialidad());
        empleadoExistente.setEmail(empleadoDTO.getEmail());
        empleadoExistente.setTelefono(empleadoDTO.getTelefono());
        empleadoExistente.setFechaContratacion(empleadoDTO.getFechaContratacion());
        
        Empleado empleadoActualizado = empleadoRepository.save(empleadoExistente);
        return empleadoMapper.toDTO(empleadoActualizado);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!empleadoRepository.existsById(id)) {
            throw new EntityNotFoundException("Empleado no encontrado con ID: " + id);
        }
        empleadoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public EmpleadoDTO activar(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con ID: " + id));
        empleado.setActivo(true);
        Empleado empleadoActivado = empleadoRepository.save(empleado);
        return empleadoMapper.toDTO(empleadoActivado);
    }

    @Override
    @Transactional
    public EmpleadoDTO desactivar(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con ID: " + id));
        empleado.setActivo(false);
        Empleado empleadoDesactivado = empleadoRepository.save(empleado);
        return empleadoMapper.toDTO(empleadoDesactivado);
    }
}