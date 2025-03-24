package com.duoc.app_spring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "INVENTARIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_inventario")
    @SequenceGenerator(name = "seq_inventario", sequenceName = "seq_inventario", allocationSize = 1)
    @Column(name = "id_producto")
    private Long idProducto;
    
    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private CategoriaInventario categoria;
    
    @Column(name = "codigo", unique = true, length = 50)
    private String codigo;
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "descripcion", length = 200)
    private String descripcion;
    
    @Column(name = "stock_actual")
    private Integer stockActual;
    
    @Column(name = "stock_minimo")
    private Integer stockMinimo;
    
    @Column(name = "unidad_medida", length = 20)
    private String unidadMedida;
    
    @Column(name = "precio_costo", precision = 10, scale = 2)
    private BigDecimal precioCosto;
    
    @Column(name = "precio_venta", precision = 10, scale = 2)
    private BigDecimal precioVenta;
    
    @Column(name = "fecha_vencimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;
    
    @Column(name = "proveedor", length = 100)
    private String proveedor;
    
    @Column(name = "activo")
    private Integer activo;
    
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    private List<MovimientoInventario> movimientos;
}