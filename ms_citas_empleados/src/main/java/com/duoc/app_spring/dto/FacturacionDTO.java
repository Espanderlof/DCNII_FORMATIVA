package com.duoc.app_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacturacionDTO {
    private Long idCita;
    private Long idCliente;
    private Long idEmpleado;
    private Long idMascota;
    private LocalDateTime fechaEmision;
    private String tipoCita;
    private Double montoEstimado;
}