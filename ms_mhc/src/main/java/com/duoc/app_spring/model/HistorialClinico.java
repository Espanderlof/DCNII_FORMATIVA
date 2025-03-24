package com.duoc.app_spring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "HISTORIAL_CLINICO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialClinico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_HISTORIAL")
    @SequenceGenerator(name = "SEQ_HISTORIAL", sequenceName = "SEQ_HISTORIAL", allocationSize = 1)
    @Column(name = "id_historial")
    private Long idHistorial;
    
    @Column(name = "id_mascota")
    private Long idMascota;
    
    @Column(name = "id_cita")
    private Long idCita;
    
    @Column(name = "id_empleado")
    private Long idEmpleado;
    
    @Column(name = "fecha_consulta")
    private LocalDate fechaConsulta;
    
    @Column(name = "motivo_consulta", length = 200)
    private String motivoConsulta;
    
    @Column(name = "diagnostico", length = 500)
    private String diagnostico;
    
    @Column(name = "tratamiento", length = 500)
    private String tratamiento;
    
    @Column(name = "observaciones", length = 1000)
    private String observaciones;
}