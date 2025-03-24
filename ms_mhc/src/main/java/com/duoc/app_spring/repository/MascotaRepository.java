package com.duoc.app_spring.repository;

import com.duoc.app_spring.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    
    List<Mascota> findByIdCliente(Long idCliente);
    
    List<Mascota> findByIdClienteAndActivoTrue(Long idCliente);
    
    Optional<Mascota> findByIdMascotaAndActivoTrue(Long idMascota);
    
    List<Mascota> findByEspecieIgnoreCase(String especie);
}