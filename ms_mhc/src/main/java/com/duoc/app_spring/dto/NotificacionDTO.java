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
public class NotificacionDTO {
    private Long idCliente;
    private String tipo;  // HISTORIAL_CREADO, RECORDATORIO_VACUNA
    private String titulo;
    private String mensaje;
    private LocalDateTime fechaCreacion;
    private String canal; // email, SMS, push
}