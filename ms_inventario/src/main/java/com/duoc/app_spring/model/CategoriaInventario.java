package com.duoc.app_spring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "CATEGORIAS_INVENTARIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaInventario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_categorias")
    @SequenceGenerator(name = "seq_categorias", sequenceName = "seq_categorias", allocationSize = 1)
    @Column(name = "id_categoria")
    private Long idCategoria;
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "descripcion", length = 200)
    private String descripcion;
    
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
    private List<Producto> productos;
}