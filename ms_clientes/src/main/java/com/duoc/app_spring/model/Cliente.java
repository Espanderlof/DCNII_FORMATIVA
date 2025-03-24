package com.duoc.app_spring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "CLIENTES")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CLIENTE")
    @SequenceGenerator(name = "SEQ_CLIENTE", sequenceName = "SEQ_CLIENTE", allocationSize = 1)
    @Column(name = "id_cliente")
    private Long idCliente;
    
    @Column(name = "rut", length = 15, unique = true)
    private String rut;
    
    @Column(name = "nombres", length = 100)
    private String nombres;
    
    @Column(name = "apellidos", length = 100)
    private String apellidos;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "telefono", length = 20)
    private String telefono;
    
    @Column(name = "direccion", length = 200)
    private String direccion;
    
    @Column(name = "ciudad", length = 50)
    private String ciudad;
    
    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;
    
    @Column(name = "activo")
    private Boolean activo;
}