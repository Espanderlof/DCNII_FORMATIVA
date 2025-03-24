package com.duoc.app_spring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "CITAS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cita {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CITA")
    @SequenceGenerator(name = "SEQ_CITA", sequenceName = "SEQ_CITA", allocationSize = 1)
    @Column(name = "id_cita")
    private Long idCita;
    
    @Column(name = "id_mascota")
    private Long idMascota;
    
    @Column(name = "id_empleado")
    private Long idEmpleado;
    
    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;
    
    @Column(name = "tipo_cita", length = 50)
    private String tipoCita;
    
    @Column(name = "motivo", length = 200)
    private String motivo;
    
    @Column(name = "estado", length = 20)
    private String estado;  // programada, completada, cancelada
    
    @Column(name = "notas", length = 500)
    private String notas;
}