package com.duoc.app_spring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "MOVIMIENTOS_INVENTARIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoInventario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_movimientos")
    @SequenceGenerator(name = "seq_movimientos", sequenceName = "seq_movimientos", allocationSize = 1)
    @Column(name = "id_movimiento")
    private Long idMovimiento;
    
    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;
    
    @Column(name = "tipo_movimiento", length = 20)
    private String tipoMovimiento;
    
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;
    
    @Column(name = "fecha_movimiento")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaMovimiento;
    
    @Column(name = "id_cita")
    private Long idCita;
    
    @Column(name = "id_empleado")
    private Long idEmpleado;
    
    @Column(name = "observacion", length = 200)
    private String observacion;
}