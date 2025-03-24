package com.duoc.app_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoInventarioDTO implements Serializable {
    private Long idMovimiento;
    private Long idProducto;
    private String nombreProducto;
    private String tipoMovimiento;
    private Integer cantidad;
    private Date fechaMovimiento;
    private Long idCita;
    private Long idEmpleado;
    private String observacion;
}