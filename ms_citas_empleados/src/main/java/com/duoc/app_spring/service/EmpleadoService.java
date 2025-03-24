package com.duoc.app_spring.service;

import com.duoc.app_spring.dto.EmpleadoCreateDTO;
import com.duoc.app_spring.dto.EmpleadoDTO;

import java.util.List;

public interface EmpleadoService {
    
    List<EmpleadoDTO> findAll();
    
    EmpleadoDTO findById(Long id);
    
    EmpleadoDTO findByRut(String rut);
    
    List<EmpleadoDTO> findByCargo(String cargo);
    
    List<EmpleadoDTO> findByEspecialidad(String especialidad);
    
    List<EmpleadoDTO> findActivos();
    
    EmpleadoDTO create(EmpleadoCreateDTO empleadoCreateDTO);
    
    EmpleadoDTO update(Long id, EmpleadoDTO empleadoDTO);
    
    void delete(Long id);
    
    EmpleadoDTO activar(Long id);
    
    EmpleadoDTO desactivar(Long id);
}