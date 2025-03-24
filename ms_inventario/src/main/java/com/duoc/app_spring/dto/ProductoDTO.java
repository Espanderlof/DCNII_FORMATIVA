package com.duoc.app_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO implements Serializable {
    private Long idProducto;
    private Long idCategoria;
    private String nombreCategoria;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Integer stockActual;
    private Integer stockMinimo;
    private String unidadMedida;
    private BigDecimal precioCosto;
    private BigDecimal precioVenta;
    private Date fechaVencimiento;
    private String proveedor;
    private Integer activo;
}