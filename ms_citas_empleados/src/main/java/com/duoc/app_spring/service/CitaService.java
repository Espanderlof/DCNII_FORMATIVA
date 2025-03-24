package com.duoc.app_spring.service;

import com.duoc.app_spring.dto.CitaCreateDTO;
import com.duoc.app_spring.dto.CitaDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaService {
    
    List<CitaDTO> findAll();
    
    CitaDTO findById(Long id);
    
    List<CitaDTO> findByMascota(Long idMascota);
    
    List<CitaDTO> findByEmpleado(Long idEmpleado);
    
    List<CitaDTO> findByEstado(String estado);
    
    List<CitaDTO> findByRangoFechas(LocalDateTime inicio, LocalDateTime fin);
    
    List<CitaDTO> findByEmpleadoAndRangoFechas(Long idEmpleado, LocalDateTime inicio, LocalDateTime fin);
    
    List<CitaDTO> findByMascotaAndEstado(Long idMascota, String estado);
    
    CitaDTO create(CitaCreateDTO citaCreateDTO);
    
    CitaDTO update(Long id, CitaDTO citaDTO);
    
    void delete(Long id);
    
    CitaDTO completarCita(Long id);
    
    CitaDTO cancelarCita(Long id);
}