package com.duoc.app_spring.repository;

import com.duoc.app_spring.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    
    List<Cita> findByIdMascota(Long idMascota);
    
    List<Cita> findByIdEmpleado(Long idEmpleado);
    
    List<Cita> findByEstado(String estado);
    
    List<Cita> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);
    
    List<Cita> findByIdEmpleadoAndFechaHoraBetween(Long idEmpleado, LocalDateTime inicio, LocalDateTime fin);
    
    List<Cita> findByIdMascotaAndEstado(Long idMascota, String estado);

    List<Cita> findByEstadoAndFechaHoraBetween(String estado, LocalDateTime inicio, LocalDateTime fin);
}