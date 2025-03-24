package com.duoc.app_spring.util;

import com.duoc.app_spring.dto.CitaDTO;
import com.duoc.app_spring.model.Cita;
import org.springframework.stereotype.Component;

@Component
public class CitaMapper {
    
    public CitaDTO toDTO(Cita cita) {
        if (cita == null) {
            return null;
        }
        
        CitaDTO dto = new CitaDTO();
        dto.setIdCita(cita.getIdCita());
        dto.setIdMascota(cita.getIdMascota());
        dto.setIdEmpleado(cita.getIdEmpleado());
        dto.setFechaHora(cita.getFechaHora());
        dto.setTipoCita(cita.getTipoCita());
        dto.setMotivo(cita.getMotivo());
        dto.setEstado(cita.getEstado());
        dto.setNotas(cita.getNotas());
        
        return dto;
    }
    
    public Cita toEntity(CitaDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Cita cita = new Cita();
        cita.setIdCita(dto.getIdCita());
        cita.setIdMascota(dto.getIdMascota());
        cita.setIdEmpleado(dto.getIdEmpleado());
        cita.setFechaHora(dto.getFechaHora());
        cita.setTipoCita(dto.getTipoCita());
        cita.setMotivo(dto.getMotivo());
        cita.setEstado(dto.getEstado());
        cita.setNotas(dto.getNotas());
        
        return cita;
    }
}