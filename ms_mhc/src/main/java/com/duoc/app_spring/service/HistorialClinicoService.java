package com.duoc.app_spring.service;

import com.duoc.app_spring.dto.HistorialClinicoCreateDTO;
import com.duoc.app_spring.dto.HistorialClinicoDTO;

import java.time.LocalDate;
import java.util.List;

public interface HistorialClinicoService {
    
    List<HistorialClinicoDTO> findAll();
    
    HistorialClinicoDTO findById(Long id);
    
    List<HistorialClinicoDTO> findByMascota(Long idMascota);
    
    List<HistorialClinicoDTO> findByMascotaOrdenado(Long idMascota);
    
    List<HistorialClinicoDTO> findByEmpleado(Long idEmpleado);
    
    List<HistorialClinicoDTO> findByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin);
    
    List<HistorialClinicoDTO> findByCita(Long idCita);
    
    HistorialClinicoDTO create(HistorialClinicoCreateDTO historialCreateDTO);
    
    HistorialClinicoDTO update(Long id, HistorialClinicoDTO historialDTO);
    
    void delete(Long id);
}