package com.duoc.app_spring.controller;

import com.duoc.app_spring.dto.CategoriaInventarioDTO;
import com.duoc.app_spring.service.CategoriaInventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaInventarioController {

    @Autowired
    private CategoriaInventarioService categoriaService;

    @GetMapping
    public ResponseEntity<List<CategoriaInventarioDTO>> getAllCategorias() {
        return ResponseEntity.ok(categoriaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaInventarioDTO> getCategoriaById(@PathVariable Long id) {
        return categoriaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<List<CategoriaInventarioDTO>> getCategoriasByNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(categoriaService.findByNombre(nombre));
    }

    @PostMapping
    public ResponseEntity<CategoriaInventarioDTO> createCategoria(@RequestBody CategoriaInventarioDTO categoriaDTO) {
        return new ResponseEntity<>(categoriaService.save(categoriaDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaInventarioDTO> updateCategoria(@PathVariable Long id, @RequestBody CategoriaInventarioDTO categoriaDTO) {
        if (!categoriaService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        categoriaDTO.setIdCategoria(id);
        return ResponseEntity.ok(categoriaService.save(categoriaDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable Long id) {
        if (categoriaService.deleteById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}