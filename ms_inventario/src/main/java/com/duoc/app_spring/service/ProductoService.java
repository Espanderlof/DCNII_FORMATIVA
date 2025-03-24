package com.duoc.app_spring.service;

import com.duoc.app_spring.dto.ProductoDTO;
import com.duoc.app_spring.model.CategoriaInventario;
import com.duoc.app_spring.model.Producto;
import com.duoc.app_spring.repository.CategoriaInventarioRepository;
import com.duoc.app_spring.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaInventarioRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<ProductoDTO> findAll() {
        return productoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProductoDTO> findById(Long id) {
        return productoRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> findByNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> findByCategoria(Long idCategoria) {
        return productoRepository.findByCategoria_IdCategoria(idCategoria).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> findProductosBajoStock() {
        return productoRepository.findProductosBajoStock().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> findByFechaVencimiento(Date fecha) {
        return productoRepository.findByFechaVencimientoBefore(fecha).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductoDTO save(ProductoDTO productoDTO) {
        Producto producto = convertToEntity(productoDTO);
        producto = productoRepository.save(producto);
        return convertToDTO(producto);
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private ProductoDTO convertToDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setIdProducto(producto.getIdProducto());
        dto.setIdCategoria(producto.getCategoria().getIdCategoria());
        dto.setNombreCategoria(producto.getCategoria().getNombre());
        dto.setCodigo(producto.getCodigo());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setStockActual(producto.getStockActual());
        dto.setStockMinimo(producto.getStockMinimo());
        dto.setUnidadMedida(producto.getUnidadMedida());
        dto.setPrecioCosto(producto.getPrecioCosto());
        dto.setPrecioVenta(producto.getPrecioVenta());
        dto.setFechaVencimiento(producto.getFechaVencimiento());
        dto.setProveedor(producto.getProveedor());
        dto.setActivo(producto.getActivo());
        return dto;
    }

    private Producto convertToEntity(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setIdProducto(dto.getIdProducto());
        
        if (dto.getIdCategoria() != null) {
            CategoriaInventario categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            producto.setCategoria(categoria);
        }
        
        producto.setCodigo(dto.getCodigo());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setStockActual(dto.getStockActual());
        producto.setStockMinimo(dto.getStockMinimo());
        producto.setUnidadMedida(dto.getUnidadMedida());
        producto.setPrecioCosto(dto.getPrecioCosto());
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setFechaVencimiento(dto.getFechaVencimiento());
        producto.setProveedor(dto.getProveedor());
        producto.setActivo(dto.getActivo());
        return producto;
    }
}