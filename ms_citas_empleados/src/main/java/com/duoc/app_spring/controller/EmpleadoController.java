package com.duoc.app_spring.controller;

import com.duoc.app_spring.dto.EmpleadoCreateDTO;
import com.duoc.app_spring.dto.EmpleadoDTO;
import com.duoc.app_spring.service.EmpleadoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping
    public ResponseEntity<List<EmpleadoDTO>> getAllEmpleados() {
        List<EmpleadoDTO> empleados = empleadoService.findAll();
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> getEmpleadoById(@PathVariable Long id) {
        EmpleadoDTO empleado = empleadoService.findById(id);
        return ResponseEntity.ok(empleado);
    }

    @GetMapping("/rut/{rut}")
    public ResponseEntity<EmpleadoDTO> getEmpleadoByRut(@PathVariable String rut) {
        EmpleadoDTO empleado = empleadoService.findByRut(rut);
        return ResponseEntity.ok(empleado);
    }

    @GetMapping("/cargo/{cargo}")
    public ResponseEntity<List<EmpleadoDTO>> getEmpleadosByCargo(@PathVariable String cargo) {
        List<EmpleadoDTO> empleados = empleadoService.findByCargo(cargo);
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/especialidad/{especialidad}")
    public ResponseEntity<List<EmpleadoDTO>> getEmpleadosByEspecialidad(@PathVariable String especialidad) {
        List<EmpleadoDTO> empleados = empleadoService.findByEspecialidad(especialidad);
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<EmpleadoDTO>> getEmpleadosActivos() {
        List<EmpleadoDTO> empleados = empleadoService.findActivos();
        return ResponseEntity.ok(empleados);
    }

    @PostMapping
    public ResponseEntity<EmpleadoDTO> createEmpleado(@Valid @RequestBody EmpleadoCreateDTO empleadoCreateDTO) {
        EmpleadoDTO nuevoEmpleado = empleadoService.create(empleadoCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEmpleado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> updateEmpleado(@PathVariable Long id, 
                                                     @Valid @RequestBody EmpleadoDTO empleadoDTO) {
        EmpleadoDTO empleadoActualizado = empleadoService.update(id, empleadoDTO);
        return ResponseEntity.ok(empleadoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmpleado(@PathVariable Long id) {
        empleadoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<EmpleadoDTO> activarEmpleado(@PathVariable Long id) {
        EmpleadoDTO empleadoActivado = empleadoService.activar(id);
        return ResponseEntity.ok(empleadoActivado);
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<EmpleadoDTO> desactivarEmpleado(@PathVariable Long id) {
        EmpleadoDTO empleadoDesactivado = empleadoService.desactivar(id);
        return ResponseEntity.ok(empleadoDesactivado);
    }
}