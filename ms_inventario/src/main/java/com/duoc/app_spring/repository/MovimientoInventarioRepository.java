package com.duoc.app_spring.repository;

import com.duoc.app_spring.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    List<MovimientoInventario> findByProducto_IdProducto(Long idProducto);
    List<MovimientoInventario> findByFechaMovimientoBetween(Date fechaInicio, Date fechaFin);
    List<MovimientoInventario> findByTipoMovimiento(String tipoMovimiento);
    List<MovimientoInventario> findByIdCita(Long idCita);
    List<MovimientoInventario> findByIdEmpleado(Long idEmpleado);
}