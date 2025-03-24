package com.duoc.app_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialClinicoCreateDTO {
    
    @NotNull(message = "El ID de la mascota es obligatorio")
    private Long idMascota;
    
    private Long idCita;
    
    @NotNull(message = "El ID del empleado es obligatorio")
    private Long idEmpleado;
    
    @NotNull(message = "La fecha de consulta es obligatoria")
    private LocalDate fechaConsulta;
    
    @NotBlank(message = "El motivo de consulta es obligatorio")
    @Size(max = 200, message = "El motivo de consulta no puede exceder los 200 caracteres")
    private String motivoConsulta;
    
    @NotBlank(message = "El diagnóstico es obligatorio")
    @Size(max = 500, message = "El diagnóstico no puede exceder los 500 caracteres")
    private String diagnostico;
    
    @Size(max = 500, message = "El tratamiento no puede exceder los 500 caracteres")
    private String tratamiento;
    
    @Size(max = 1000, message = "Las observaciones no pueden exceder los 1000 caracteres")
    private String observaciones;
}