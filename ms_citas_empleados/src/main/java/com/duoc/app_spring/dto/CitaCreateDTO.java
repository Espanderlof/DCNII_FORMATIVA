package com.duoc.app_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaCreateDTO {
    
    @NotNull(message = "El ID de la mascota es obligatorio")
    private Long idMascota;
    
    @NotNull(message = "El ID del empleado es obligatorio")
    private Long idEmpleado;
    
    @NotNull(message = "La fecha y hora son obligatorias")
    private LocalDateTime fechaHora;
    
    @NotBlank(message = "El tipo de cita es obligatorio")
    @Size(max = 50, message = "El tipo de cita no puede exceder los 50 caracteres")
    private String tipoCita;
    
    @Size(max = 200, message = "El motivo no puede exceder los 200 caracteres")
    private String motivo;
    
    @Size(max = 500, message = "Las notas no pueden exceder los 500 caracteres")
    private String notas;
}