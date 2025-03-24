package com.duoc.app_spring.util;

import com.duoc.app_spring.dto.ClienteDTO;
import com.duoc.app_spring.model.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {
    
    public ClienteDTO toDTO(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        
        ClienteDTO dto = new ClienteDTO();
        dto.setIdCliente(cliente.getIdCliente());
        dto.setRut(cliente.getRut());
        dto.setNombres(cliente.getNombres());
        dto.setApellidos(cliente.getApellidos());
        dto.setEmail(cliente.getEmail());
        dto.setTelefono(cliente.getTelefono());
        dto.setDireccion(cliente.getDireccion());
        dto.setCiudad(cliente.getCiudad());
        dto.setFechaRegistro(cliente.getFechaRegistro());
        dto.setActivo(cliente.getActivo());
        
        return dto;
    }
    
    public Cliente toEntity(ClienteDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Cliente cliente = new Cliente();
        cliente.setIdCliente(dto.getIdCliente());
        cliente.setRut(dto.getRut());
        cliente.setNombres(dto.getNombres());
        cliente.setApellidos(dto.getApellidos());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        cliente.setCiudad(dto.getCiudad());
        cliente.setFechaRegistro(dto.getFechaRegistro());
        cliente.setActivo(dto.getActivo());
        
        return cliente;
    }
}