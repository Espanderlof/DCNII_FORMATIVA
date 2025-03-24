package com.duoc.app_spring.controller;

import com.duoc.app_spring.dto.MovimientoInventarioDTO;
import com.duoc.app_spring.service.MovimientoInventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@CrossOrigin(origins = "*")
public class MovimientoInventarioController {

    @Autowired
    private MovimientoInventarioService movimientoService;

    @GetMapping
    public ResponseEntity<List<MovimientoInventarioDTO>> getAllMovimientos() {
        return ResponseEntity.ok(movimientoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoInventarioDTO> getMovimientoById(@PathVariable Long id) {
        return movimientoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<MovimientoInventarioDTO>> getMovimientosByProducto(@PathVariable Long idProducto) {
        return ResponseEntity.ok(movimientoService.findByProducto(idProducto));
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<MovimientoInventarioDTO>> getMovimientosByPeriodo(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin) {
        return ResponseEntity.ok(movimientoService.findByFechaBetween(fechaInicio, fechaFin));
    }

    @GetMapping("/tipo/{tipoMovimiento}")
    public ResponseEntity<List<MovimientoInventarioDTO>> getMovimientosByTipo(@PathVariable String tipoMovimiento) {
        return ResponseEntity.ok(movimientoService.findByTipoMovimiento(tipoMovimiento));
    }

    @PostMapping
    public ResponseEntity<MovimientoInventarioDTO> createMovimiento(@RequestBody MovimientoInventarioDTO movimientoDTO) {
        try {
            return new ResponseEntity<>(movimientoService.save(movimientoDTO), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimientoInventarioDTO> updateMovimiento(@PathVariable Long id, @RequestBody MovimientoInventarioDTO movimientoDTO) {
        if (!movimientoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            movimientoDTO.setIdMovimiento(id);
            return ResponseEntity.ok(movimientoService.save(movimientoDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovimiento(@PathVariable Long id) {
        try {
            if (movimientoService.deleteById(id)) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}