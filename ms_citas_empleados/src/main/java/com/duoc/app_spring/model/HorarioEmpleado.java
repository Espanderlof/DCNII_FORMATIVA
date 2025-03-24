package com.duoc.app_spring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "HORARIOS_EMPLEADOS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorarioEmpleado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_HORARIO")
    @SequenceGenerator(name = "SEQ_HORARIO", sequenceName = "SEQ_HORARIO", allocationSize = 1)
    @Column(name = "id_horario")
    private Long idHorario;
    
    @Column(name = "id_empleado")
    private Long idEmpleado;
    
    @Column(name = "dia_semana")
    private Integer diaSemana;  // 1: Lunes, 2: Martes, ..., 7: Domingo
    
    @Column(name = "hora_inicio")
    private LocalTime horaInicio;
    
    @Column(name = "hora_fin")
    private LocalTime horaFin;
}