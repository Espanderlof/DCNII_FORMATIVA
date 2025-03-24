package com.duoc.app_spring.service;

import com.duoc.app_spring.dto.MovimientoInventarioDTO;
import com.duoc.app_spring.model.MovimientoInventario;
import com.duoc.app_spring.model.Producto;
import com.duoc.app_spring.repository.MovimientoInventarioRepository;
import com.duoc.app_spring.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovimientoInventarioService {

    @Autowired
    private MovimientoInventarioRepository movimientoRepository;

    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private AzureFunctionService azureFunctionService;

    @Transactional(readOnly = true)
    public List<MovimientoInventarioDTO> findAll() {
        return movimientoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<MovimientoInventarioDTO> findById(Long id) {
        return movimientoRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public List<MovimientoInventarioDTO> findByProducto(Long idProducto) {
        return movimientoRepository.findByProducto_IdProducto(idProducto).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovimientoInventarioDTO> findByFechaBetween(Date fechaInicio, Date fechaFin) {
        return movimientoRepository.findByFechaMovimientoBetween(fechaInicio, fechaFin).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovimientoInventarioDTO> findByTipoMovimiento(String tipoMovimiento) {
        return movimientoRepository.findByTipoMovimiento(tipoMovimiento).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MovimientoInventarioDTO save(MovimientoInventarioDTO movimientoDTO) {
        MovimientoInventario movimiento = convertToEntity(movimientoDTO);
        
        // Actualizar el stock del producto
        Producto producto = movimiento.getProducto();
        Integer stockAnterior = producto.getStockActual();
        
        if ("ENTRADA".equals(movimiento.getTipoMovimiento())) {
            producto.setStockActual(producto.getStockActual() + movimiento.getCantidad());
        } else if ("SALIDA".equals(movimiento.getTipoMovimiento())) {
            if (producto.getStockActual() >= movimiento.getCantidad()) {
                producto.setStockActual(producto.getStockActual() - movimiento.getCantidad());
            } else {
                throw new RuntimeException("Stock insuficiente para realizar la salida");
            }
        }
        
        // Guardar el producto actualizado
        productoRepository.save(producto);
        
        // Verificar si el stock está por debajo del mínimo después de la actualización
        verificarYNotificarStockBajo(producto, stockAnterior);
        
        // Guardar el movimiento
        movimiento = movimientoRepository.save(movimiento);
        return convertToDTO(movimiento);
    }

    /**
     * Verifica si el stock está por debajo del mínimo y envía notificación si es necesario
     * @param producto Producto actualizado
     * @param stockAnterior Stock antes de la actualización
     */
    private void verificarYNotificarStockBajo(Producto producto, Integer stockAnterior) {
        // Si el stock anterior estaba por encima del mínimo y ahora está por debajo o igual, notificamos
        if ((stockAnterior > producto.getStockMinimo()) && 
            (producto.getStockActual() <= producto.getStockMinimo())) {
            
            // Enviar alerta de stock bajo
            azureFunctionService.enviarAlertaStockBajo(
                producto.getIdProducto(),
                producto.getNombre(),
                producto.getStockActual(),
                producto.getStockMinimo()
            );
        }
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (movimientoRepository.existsById(id)) {
            // Obtener el movimiento para revertir el cambio en el stock
            Optional<MovimientoInventario> movimientoOpt = movimientoRepository.findById(id);
            if (movimientoOpt.isPresent()) {
                MovimientoInventario movimiento = movimientoOpt.get();
                Producto producto = movimiento.getProducto();
                Integer stockAnterior = producto.getStockActual();
                
                // Revertir el cambio en el stock
                if ("ENTRADA".equals(movimiento.getTipoMovimiento())) {
                    producto.setStockActual(producto.getStockActual() - movimiento.getCantidad());
                } else if ("SALIDA".equals(movimiento.getTipoMovimiento())) {
                    producto.setStockActual(producto.getStockActual() + movimiento.getCantidad());
                }
                
                // Guardar el producto actualizado
                productoRepository.save(producto);
                
                // Verificar si el stock está por debajo del mínimo después de la actualización
                verificarYNotificarStockBajo(producto, stockAnterior);
            }
            
            movimientoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private MovimientoInventarioDTO convertToDTO(MovimientoInventario movimiento) {
        MovimientoInventarioDTO dto = new MovimientoInventarioDTO();
        dto.setIdMovimiento(movimiento.getIdMovimiento());
        dto.setIdProducto(movimiento.getProducto().getIdProducto());
        dto.setNombreProducto(movimiento.getProducto().getNombre());
        dto.setTipoMovimiento(movimiento.getTipoMovimiento());
        dto.setCantidad(movimiento.getCantidad());
        dto.setFechaMovimiento(movimiento.getFechaMovimiento());
        dto.setIdCita(movimiento.getIdCita());
        dto.setIdEmpleado(movimiento.getIdEmpleado());
        dto.setObservacion(movimiento.getObservacion());
        return dto;
    }

    private MovimientoInventario convertToEntity(MovimientoInventarioDTO dto) {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setIdMovimiento(dto.getIdMovimiento());
        
        if (dto.getIdProducto() != null) {
            Producto producto = productoRepository.findById(dto.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            movimiento.setProducto(producto);
        }
        
        movimiento.setTipoMovimiento(dto.getTipoMovimiento());
        movimiento.setCantidad(dto.getCantidad());
        movimiento.setFechaMovimiento(dto.getFechaMovimiento() != null ? dto.getFechaMovimiento() : new Date());
        movimiento.setIdCita(dto.getIdCita());
        movimiento.setIdEmpleado(dto.getIdEmpleado());
        movimiento.setObservacion(dto.getObservacion());
        return movimiento;
    }
}