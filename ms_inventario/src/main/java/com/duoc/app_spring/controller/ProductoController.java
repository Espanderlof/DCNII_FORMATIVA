package com.duoc.app_spring.controller;

import com.duoc.app_spring.dto.ProductoDTO;
import com.duoc.app_spring.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> getAllProductos() {
        return ResponseEntity.ok(productoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> getProductoById(@PathVariable Long id) {
        return productoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<List<ProductoDTO>> getProductosByNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(productoService.findByNombre(nombre));
    }

    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<List<ProductoDTO>> getProductosByCategoria(@PathVariable Long idCategoria) {
        return ResponseEntity.ok(productoService.findByCategoria(idCategoria));
    }

    @GetMapping("/bajo-stock")
    public ResponseEntity<List<ProductoDTO>> getProductosBajoStock() {
        return ResponseEntity.ok(productoService.findProductosBajoStock());
    }

    @GetMapping("/vencimiento-antes/{fecha}")
    public ResponseEntity<List<ProductoDTO>> getProductosByVencimiento(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecha) {
        return ResponseEntity.ok(productoService.findByFechaVencimiento(fecha));
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> createProducto(@RequestBody ProductoDTO productoDTO) {
        return new ResponseEntity<>(productoService.save(productoDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> updateProducto(@PathVariable Long id, @RequestBody ProductoDTO productoDTO) {
        if (!productoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        productoDTO.setIdProducto(id);
        return ResponseEntity.ok(productoService.save(productoDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        if (productoService.deleteById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}