package com.duoc.app_spring.controller;

import com.duoc.app_spring.dto.MascotaCreateDTO;
import com.duoc.app_spring.dto.MascotaDTO;
import com.duoc.app_spring.service.MascotaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;

    public MascotaController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    @GetMapping
    public ResponseEntity<List<MascotaDTO>> getAllMascotas() {
        List<MascotaDTO> mascotas = mascotaService.findAll();
        return ResponseEntity.ok(mascotas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MascotaDTO> getMascotaById(@PathVariable Long id) {
        MascotaDTO mascota = mascotaService.findById(id);
        return ResponseEntity.ok(mascota);
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<MascotaDTO>> getMascotasByCliente(@PathVariable Long idCliente) {
        List<MascotaDTO> mascotas = mascotaService.findByCliente(idCliente);
        return ResponseEntity.ok(mascotas);
    }

    @GetMapping("/cliente/{idCliente}/activas")
    public ResponseEntity<List<MascotaDTO>> getMascotasActivasByCliente(@PathVariable Long idCliente) {
        List<MascotaDTO> mascotas = mascotaService.findActivasByCliente(idCliente);
        return ResponseEntity.ok(mascotas);
    }

    @GetMapping("/especie/{especie}")
    public ResponseEntity<List<MascotaDTO>> getMascotasByEspecie(@PathVariable String especie) {
        List<MascotaDTO> mascotas = mascotaService.findByEspecie(especie);
        return ResponseEntity.ok(mascotas);
    }

    @PostMapping
    public ResponseEntity<MascotaDTO> createMascota(@Valid @RequestBody MascotaCreateDTO mascotaCreateDTO) {
        MascotaDTO nuevaMascota = mascotaService.create(mascotaCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMascota);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MascotaDTO> updateMascota(@PathVariable Long id, 
                                                    @Valid @RequestBody MascotaDTO mascotaDTO) {
        MascotaDTO mascotaActualizada = mascotaService.update(id, mascotaDTO);
        return ResponseEntity.ok(mascotaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMascota(@PathVariable Long id) {
        mascotaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<MascotaDTO> activarMascota(@PathVariable Long id) {
        MascotaDTO mascotaActivada = mascotaService.activar(id);
        return ResponseEntity.ok(mascotaActivada);
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<MascotaDTO> desactivarMascota(@PathVariable Long id) {
        MascotaDTO mascotaDesactivada = mascotaService.desactivar(id);
        return ResponseEntity.ok(mascotaDesactivada);
    }
}