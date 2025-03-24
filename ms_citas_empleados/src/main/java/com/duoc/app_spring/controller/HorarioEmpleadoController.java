package com.duoc.app_spring.controller;

import com.duoc.app_spring.dto.HorarioEmpleadoDTO;
import com.duoc.app_spring.service.HorarioEmpleadoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
public class HorarioEmpleadoController {

    private final HorarioEmpleadoService horarioService;

    public HorarioEmpleadoController(HorarioEmpleadoService horarioService) {
        this.horarioService = horarioService;
    }

    @GetMapping
    public ResponseEntity<List<HorarioEmpleadoDTO>> getAllHorarios() {
        List<HorarioEmpleadoDTO> horarios = horarioService.findAll();
        return ResponseEntity.ok(horarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioEmpleadoDTO> getHorarioById(@PathVariable Long id) {
        HorarioEmpleadoDTO horario = horarioService.findById(id);
        return ResponseEntity.ok(horario);
    }

    @GetMapping("/empleado/{idEmpleado}")
    public ResponseEntity<List<HorarioEmpleadoDTO>> getHorariosByEmpleado(@PathVariable Long idEmpleado) {
        List<HorarioEmpleadoDTO> horarios = horarioService.findByEmpleado(idEmpleado);
        return ResponseEntity.ok(horarios);
    }

    @GetMapping("/empleado/{idEmpleado}/dia/{diaSemana}")
    public ResponseEntity<List<HorarioEmpleadoDTO>> getHorariosByEmpleadoAndDia(
            @PathVariable Long idEmpleado, 
            @PathVariable Integer diaSemana) {
        List<HorarioEmpleadoDTO> horarios = horarioService.findByEmpleadoAndDia(idEmpleado, diaSemana);
        return ResponseEntity.ok(horarios);
    }

    @PostMapping
    public ResponseEntity<HorarioEmpleadoDTO> createHorario(@Valid @RequestBody HorarioEmpleadoDTO horarioDTO) {
        HorarioEmpleadoDTO nuevoHorario = horarioService.create(horarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoHorario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HorarioEmpleadoDTO> updateHorario(
            @PathVariable Long id, 
            @Valid @RequestBody HorarioEmpleadoDTO horarioDTO) {
        HorarioEmpleadoDTO horarioActualizado = horarioService.update(id, horarioDTO);
        return ResponseEntity.ok(horarioActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHorario(@PathVariable Long id) {
        horarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/empleado/{idEmpleado}")
    public ResponseEntity<Void> deleteHorariosByEmpleado(@PathVariable Long idEmpleado) {
        horarioService.deleteByEmpleado(idEmpleado);
        return ResponseEntity.noContent().build();
    }
}