package com.duoc.app_spring.util;

import com.duoc.app_spring.dto.EmpleadoDTO;
import com.duoc.app_spring.model.Empleado;
import org.springframework.stereotype.Component;

@Component
public class EmpleadoMapper {
    
    public EmpleadoDTO toDTO(Empleado empleado) {
        if (empleado == null) {
            return null;
        }
        
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setIdEmpleado(empleado.getIdEmpleado());
        dto.setRut(empleado.getRut());
        dto.setNombres(empleado.getNombres());
        dto.setApellidos(empleado.getApellidos());
        dto.setCargo(empleado.getCargo());
        dto.setEspecialidad(empleado.getEspecialidad());
        dto.setEmail(empleado.getEmail());
        dto.setTelefono(empleado.getTelefono());
        dto.setFechaContratacion(empleado.getFechaContratacion());
        dto.setActivo(empleado.getActivo());
        
        return dto;
    }
    
    public Empleado toEntity(EmpleadoDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Empleado empleado = new Empleado();
        empleado.setIdEmpleado(dto.getIdEmpleado());
        empleado.setRut(dto.getRut());
        empleado.setNombres(dto.getNombres());
        empleado.setApellidos(dto.getApellidos());
        empleado.setCargo(dto.getCargo());
        empleado.setEspecialidad(dto.getEspecialidad());
        empleado.setEmail(dto.getEmail());
        empleado.setTelefono(dto.getTelefono());
        empleado.setFechaContratacion(dto.getFechaContratacion());
        empleado.setActivo(dto.getActivo());
        
        return empleado;
    }
}