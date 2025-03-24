package com.duoc.app_spring.controller;

import com.duoc.app_spring.dto.CitaCreateDTO;
import com.duoc.app_spring.dto.CitaDTO;
import com.duoc.app_spring.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping
    public ResponseEntity<List<CitaDTO>> getAllCitas() {
        List<CitaDTO> citas = citaService.findAll();
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaDTO> getCitaById(@PathVariable Long id) {
        CitaDTO cita = citaService.findById(id);
        return ResponseEntity.ok(cita);
    }

    @GetMapping("/mascota/{idMascota}")
    public ResponseEntity<List<CitaDTO>> getCitasByMascota(@PathVariable Long idMascota) {
        List<CitaDTO> citas = citaService.findByMascota(idMascota);
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/empleado/{idEmpleado}")
    public ResponseEntity<List<CitaDTO>> getCitasByEmpleado(@PathVariable Long idEmpleado) {
        List<CitaDTO> citas = citaService.findByEmpleado(idEmpleado);
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<CitaDTO>> getCitasByEstado(@PathVariable String estado) {
        List<CitaDTO> citas = citaService.findByEstado(estado);
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/fechas")
    public ResponseEntity<List<CitaDTO>> getCitasByRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<CitaDTO> citas = citaService.findByRangoFechas(inicio, fin);
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/empleado/{idEmpleado}/fechas")
    public ResponseEntity<List<CitaDTO>> getCitasByEmpleadoAndRangoFechas(
            @PathVariable Long idEmpleado,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<CitaDTO> citas = citaService.findByEmpleadoAndRangoFechas(idEmpleado, inicio, fin);
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/mascota/{idMascota}/estado/{estado}")
    public ResponseEntity<List<CitaDTO>> getCitasByMascotaAndEstado(
            @PathVariable Long idMascota,
            @PathVariable String estado) {
        List<CitaDTO> citas = citaService.findByMascotaAndEstado(idMascota, estado);
        return ResponseEntity.ok(citas);
    }

    @PostMapping
    public ResponseEntity<CitaDTO> createCita(@Valid @RequestBody CitaCreateDTO citaCreateDTO) {
        CitaDTO nuevaCita = citaService.create(citaCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCita);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CitaDTO> updateCita(
            @PathVariable Long id, 
            @Valid @RequestBody CitaDTO citaDTO) {
        CitaDTO citaActualizada = citaService.update(id, citaDTO);
        return ResponseEntity.ok(citaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCita(@PathVariable Long id) {
        citaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/completar")
    public ResponseEntity<CitaDTO> completarCita(@PathVariable Long id) {
        CitaDTO citaCompletada = citaService.completarCita(id);
        return ResponseEntity.ok(citaCompletada);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<CitaDTO> cancelarCita(@PathVariable Long id) {
        CitaDTO citaCancelada = citaService.cancelarCita(id);
        return ResponseEntity.ok(citaCancelada);
    }
}