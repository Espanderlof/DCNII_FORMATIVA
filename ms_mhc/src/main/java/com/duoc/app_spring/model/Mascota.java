package com.duoc.app_spring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "MASCOTAS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mascota {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MASCOTA")
    @SequenceGenerator(name = "SEQ_MASCOTA", sequenceName = "SEQ_MASCOTA", allocationSize = 1)
    @Column(name = "id_mascota")
    private Long idMascota;
    
    @Column(name = "id_cliente")
    private Long idCliente;
    
    @Column(name = "nombre", length = 50)
    private String nombre;
    
    @Column(name = "especie", length = 50)
    private String especie;
    
    @Column(name = "raza", length = 50)
    private String raza;
    
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
    
    @Column(name = "sexo", length = 1)
    private String sexo;
    
    @Column(name = "color", length = 50)
    private String color;
    
    @Column(name = "peso")
    private Double peso;
    
    @Column(name = "activo")
    private Boolean activo;
    
    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;
}