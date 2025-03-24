package com.duoc.app_spring.controller;

import com.duoc.app_spring.dto.HistorialClinicoCreateDTO;
import com.duoc.app_spring.dto.HistorialClinicoDTO;
import com.duoc.app_spring.service.HistorialClinicoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/historial-clinico")
public class HistorialClinicoController {

    private final HistorialClinicoService historialService;

    public HistorialClinicoController(HistorialClinicoService historialService) {
        this.historialService = historialService;
    }

    @GetMapping
    public ResponseEntity<List<HistorialClinicoDTO>> getAllHistorialClinico() {
        List<HistorialClinicoDTO> registros = historialService.findAll();
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistorialClinicoDTO> getHistorialClinicoById(@PathVariable Long id) {
        HistorialClinicoDTO registro = historialService.findById(id);
        return ResponseEntity.ok(registro);
    }

    @GetMapping("/mascota/{idMascota}")
    public ResponseEntity<List<HistorialClinicoDTO>> getHistorialClinicoByMascota(@PathVariable Long idMascota) {
        List<HistorialClinicoDTO> registros = historialService.findByMascota(idMascota);
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/mascota/{idMascota}/ordenado")
    public ResponseEntity<List<HistorialClinicoDTO>> getHistorialClinicoByMascotaOrdenado(@PathVariable Long idMascota) {
        List<HistorialClinicoDTO> registros = historialService.findByMascotaOrdenado(idMascota);
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/empleado/{idEmpleado}")
    public ResponseEntity<List<HistorialClinicoDTO>> getHistorialClinicoByEmpleado(@PathVariable Long idEmpleado) {
        List<HistorialClinicoDTO> registros = historialService.findByEmpleado(idEmpleado);
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/cita/{idCita}")
    public ResponseEntity<List<HistorialClinicoDTO>> getHistorialClinicoByCita(@PathVariable Long idCita) {
        List<HistorialClinicoDTO> registros = historialService.findByCita(idCita);
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/fechas")
    public ResponseEntity<List<HistorialClinicoDTO>> getHistorialClinicoByRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<HistorialClinicoDTO> registros = historialService.findByRangoFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(registros);
    }

    @PostMapping
    public ResponseEntity<HistorialClinicoDTO> createHistorialClinico(
            @Valid @RequestBody HistorialClinicoCreateDTO historialCreateDTO) {
        HistorialClinicoDTO nuevoRegistro = historialService.create(historialCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRegistro);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HistorialClinicoDTO> updateHistorialClinico(
            @PathVariable Long id, 
            @Valid @RequestBody HistorialClinicoDTO historialDTO) {
        HistorialClinicoDTO registroActualizado = historialService.update(id, historialDTO);
        return ResponseEntity.ok(registroActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistorialClinico(@PathVariable Long id) {
        historialService.delete(id);
        return ResponseEntity.noContent().build();
    }
}