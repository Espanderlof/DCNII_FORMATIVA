package com.duoc.app_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaInventarioDTO implements Serializable {
    private Long idCategoria;
    private String nombre;
    private String descripcion;
}