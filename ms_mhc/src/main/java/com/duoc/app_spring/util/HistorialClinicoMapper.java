package com.duoc.app_spring.util;

import com.duoc.app_spring.dto.HistorialClinicoDTO;
import com.duoc.app_spring.model.HistorialClinico;
import org.springframework.stereotype.Component;

@Component
public class HistorialClinicoMapper {
    
    public HistorialClinicoDTO toDTO(HistorialClinico historial) {
        if (historial == null) {
            return null;
        }
        
        HistorialClinicoDTO dto = new HistorialClinicoDTO();
        dto.setIdHistorial(historial.getIdHistorial());
        dto.setIdMascota(historial.getIdMascota());
        dto.setIdCita(historial.getIdCita());
        dto.setIdEmpleado(historial.getIdEmpleado());
        dto.setFechaConsulta(historial.getFechaConsulta());
        dto.setMotivoConsulta(historial.getMotivoConsulta());
        dto.setDiagnostico(historial.getDiagnostico());
        dto.setTratamiento(historial.getTratamiento());
        dto.setObservaciones(historial.getObservaciones());
        
        return dto;
    }
    
    public HistorialClinico toEntity(HistorialClinicoDTO dto) {
        if (dto == null) {
            return null;
        }
        
        HistorialClinico historial = new HistorialClinico();
        historial.setIdHistorial(dto.getIdHistorial());
        historial.setIdMascota(dto.getIdMascota());
        historial.setIdCita(dto.getIdCita());
        historial.setIdEmpleado(dto.getIdEmpleado());
        historial.setFechaConsulta(dto.getFechaConsulta());
        historial.setMotivoConsulta(dto.getMotivoConsulta());
        historial.setDiagnostico(dto.getDiagnostico());
        historial.setTratamiento(dto.getTratamiento());
        historial.setObservaciones(dto.getObservaciones());
        
        return historial;
    }
}