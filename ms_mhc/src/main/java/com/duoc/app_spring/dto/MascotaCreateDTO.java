package com.duoc.app_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MascotaCreateDTO {
    
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long idCliente;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
    private String nombre;
    
    @NotBlank(message = "La especie es obligatoria")
    @Size(max = 50, message = "La especie no puede exceder los 50 caracteres")
    private String especie;
    
    @Size(max = 50, message = "La raza no puede exceder los 50 caracteres")
    private String raza;
    
    private LocalDate fechaNacimiento;
    
    @Pattern(regexp = "^[MH]$", message = "El sexo debe ser 'M' para macho o 'H' para hembra")
    private String sexo;
    
    @Size(max = 50, message = "El color no puede exceder los 50 caracteres")
    private String color;
    
    @DecimalMin(value = "0.01", message = "El peso debe ser mayor a 0")
    @DecimalMax(value = "999.99", message = "El peso no puede exceder 999.99 kg")
    private Double peso;
}