package com.duoc.app_spring.service;

import com.duoc.app_spring.dto.HorarioEmpleadoDTO;

import java.util.List;

public interface HorarioEmpleadoService {
    
    List<HorarioEmpleadoDTO> findAll();
    
    HorarioEmpleadoDTO findById(Long id);
    
    List<HorarioEmpleadoDTO> findByEmpleado(Long idEmpleado);
    
    List<HorarioEmpleadoDTO> findByEmpleadoAndDia(Long idEmpleado, Integer diaSemana);
    
    HorarioEmpleadoDTO create(HorarioEmpleadoDTO horarioDTO);
    
    HorarioEmpleadoDTO update(Long id, HorarioEmpleadoDTO horarioDTO);
    
    void delete(Long id);
    
    void deleteByEmpleado(Long idEmpleado);
}