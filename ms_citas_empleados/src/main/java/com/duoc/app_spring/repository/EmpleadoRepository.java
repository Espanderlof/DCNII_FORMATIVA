package com.duoc.app_spring.repository;

import com.duoc.app_spring.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    
    Optional<Empleado> findByRut(String rut);
    
    boolean existsByRut(String rut);
    
    boolean existsByEmail(String email);
    
    List<Empleado> findByCargo(String cargo);
    
    List<Empleado> findByEspecialidad(String especialidad);
    
    List<Empleado> findByActivoTrue();
}