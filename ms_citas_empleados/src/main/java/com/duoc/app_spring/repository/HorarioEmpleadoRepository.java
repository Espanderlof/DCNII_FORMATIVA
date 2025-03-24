package com.duoc.app_spring.repository;

import com.duoc.app_spring.model.HorarioEmpleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioEmpleadoRepository extends JpaRepository<HorarioEmpleado, Long> {
    
    List<HorarioEmpleado> findByIdEmpleado(Long idEmpleado);
    
    List<HorarioEmpleado> findByIdEmpleadoAndDiaSemana(Long idEmpleado, Integer diaSemana);
    
    void deleteByIdEmpleado(Long idEmpleado);
}