package com.duoc.app_spring.repository;

import com.duoc.app_spring.model.CategoriaInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoriaInventarioRepository extends JpaRepository<CategoriaInventario, Long> {
    List<CategoriaInventario> findByNombreContainingIgnoreCase(String nombre);
    boolean existsByNombre(String nombre);
}