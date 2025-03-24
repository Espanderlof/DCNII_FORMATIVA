package com.duoc.app_spring.repository;

import com.duoc.app_spring.model.HistorialClinico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HistorialClinicoRepository extends JpaRepository<HistorialClinico, Long> {
    
    List<HistorialClinico> findByIdMascota(Long idMascota);
    
    List<HistorialClinico> findByIdMascotaOrderByFechaConsultaDesc(Long idMascota);
    
    List<HistorialClinico> findByIdEmpleado(Long idEmpleado);
    
    List<HistorialClinico> findByFechaConsultaBetween(LocalDate fechaInicio, LocalDate fechaFin);
    
    List<HistorialClinico> findByIdCita(Long idCita);

    List<HistorialClinico> findByFechaConsultaBetweenAndTratamientoContainingIgnoreCase(
            LocalDate fechaInicio, LocalDate fechaFin, String tratamiento);
}