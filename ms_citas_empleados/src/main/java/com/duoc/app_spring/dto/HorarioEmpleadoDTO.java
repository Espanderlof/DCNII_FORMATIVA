package com.duoc.app_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorarioEmpleadoDTO {
    
    private Long idHorario;
    
    @NotNull(message = "El ID del empleado es obligatorio")
    private Long idEmpleado;
    
    @NotNull(message = "El día de la semana es obligatorio")
    @Min(value = 1, message = "El día de la semana debe ser entre 1 y 7")
    @Max(value = 7, message = "El día de la semana debe ser entre 1 y 7")
    private Integer diaSemana;
    
    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;
    
    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;
}