package com.duoc.app_spring.controller;

import com.duoc.app_spring.dto.ClienteCreateDTO;
import com.duoc.app_spring.dto.ClienteDTO;
import com.duoc.app_spring.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<List<ClienteDTO>> getAllClientes() {
        List<ClienteDTO> clientes = clienteService.findAll();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> getClienteById(@PathVariable Long id) {
        ClienteDTO cliente = clienteService.findById(id);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/rut/{rut}")
    public ResponseEntity<ClienteDTO> getClienteByRut(@PathVariable String rut) {
        ClienteDTO cliente = clienteService.findByRut(rut);
        return ResponseEntity.ok(cliente);
    }

    @PostMapping
    public ResponseEntity<ClienteDTO> createCliente(@Valid @RequestBody ClienteCreateDTO clienteCreateDTO) {
        ClienteDTO nuevoCliente = clienteService.create(clienteCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> updateCliente(@PathVariable Long id, 
                                                    @Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteDTO clienteActualizado = clienteService.update(id, clienteDTO);
        return ResponseEntity.ok(clienteActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ClienteDTO> activarCliente(@PathVariable Long id) {
        ClienteDTO clienteActivado = clienteService.activar(id);
        return ResponseEntity.ok(clienteActivado);
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ClienteDTO> desactivarCliente(@PathVariable Long id) {
        ClienteDTO clienteDesactivado = clienteService.desactivar(id);
        return ResponseEntity.ok(clienteDesactivado);
    }
}