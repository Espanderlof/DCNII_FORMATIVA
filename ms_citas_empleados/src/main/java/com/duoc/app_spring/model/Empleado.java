package com.duoc.app_spring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "EMPLEADOS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empleado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_EMPLEADO")
    @SequenceGenerator(name = "SEQ_EMPLEADO", sequenceName = "SEQ_EMPLEADO", allocationSize = 1)
    @Column(name = "id_empleado")
    private Long idEmpleado;
    
    @Column(name = "rut", length = 15, unique = true)
    private String rut;
    
    @Column(name = "nombres", length = 100)
    private String nombres;
    
    @Column(name = "apellidos", length = 100)
    private String apellidos;
    
    @Column(name = "cargo", length = 50)
    private String cargo;
    
    @Column(name = "especialidad", length = 100)
    private String especialidad;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "telefono", length = 20)
    private String telefono;
    
    @Column(name = "fecha_contratacion")
    private LocalDate fechaContratacion;
    
    @Column(name = "activo")
    private Boolean activo;
}