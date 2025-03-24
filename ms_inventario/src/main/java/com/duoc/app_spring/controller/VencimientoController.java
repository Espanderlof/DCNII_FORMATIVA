package com.duoc.app_spring.controller;

import com.duoc.app_spring.service.VencimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vencimientos")
@CrossOrigin(origins = "*")
public class VencimientoController {

    @Autowired
    private VencimientoService vencimientoService;

    /**
     * Endpoint para ejecutar manualmente la verificación de productos por vencer
     * @return ResponseEntity con mensaje de confirmación
     */
    @GetMapping("/verificar")
    public ResponseEntity<String> verificarProductosPorVencer() {
        try {
            vencimientoService.verificarProductosPorVencer();
            return ResponseEntity.ok("Verificación de productos por vencer ejecutada correctamente");
        } catch (Exception e) {
            // Log del error
            System.err.println("Error al verificar productos por vencer: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al verificar productos por vencer: " + e.getMessage());
        }
    }
}