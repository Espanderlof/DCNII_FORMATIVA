package com.duoc.app_spring.util;

import com.duoc.app_spring.dto.HorarioEmpleadoDTO;
import com.duoc.app_spring.model.HorarioEmpleado;
import org.springframework.stereotype.Component;

@Component
public class HorarioEmpleadoMapper {
    
    public HorarioEmpleadoDTO toDTO(HorarioEmpleado horario) {
        if (horario == null) {
            return null;
        }
        
        HorarioEmpleadoDTO dto = new HorarioEmpleadoDTO();
        dto.setIdHorario(horario.getIdHorario());
        dto.setIdEmpleado(horario.getIdEmpleado());
        dto.setDiaSemana(horario.getDiaSemana());
        dto.setHoraInicio(horario.getHoraInicio());
        dto.setHoraFin(horario.getHoraFin());
        
        return dto;
    }
    
    public HorarioEmpleado toEntity(HorarioEmpleadoDTO dto) {
        if (dto == null) {
            return null;
        }
        
        HorarioEmpleado horario = new HorarioEmpleado();
        horario.setIdHorario(dto.getIdHorario());
        horario.setIdEmpleado(dto.getIdEmpleado());
        horario.setDiaSemana(dto.getDiaSemana());
        horario.setHoraInicio(dto.getHoraInicio());
        horario.setHoraFin(dto.getHoraFin());
        
        return horario;
    }
}