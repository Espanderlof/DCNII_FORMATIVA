package com.duoc.app_spring.repository;

import com.duoc.app_spring.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    List<Producto> findByCategoria_IdCategoria(Long idCategoria);
    List<Producto> findByStockActualLessThanEqual(Integer stockMinimo);
    List<Producto> findByFechaVencimientoBefore(Date fecha);
    boolean existsByCodigo(String codigo);
    
    @Query("SELECT p FROM Producto p WHERE p.stockActual <= p.stockMinimo")
    List<Producto> findProductosBajoStock();
}