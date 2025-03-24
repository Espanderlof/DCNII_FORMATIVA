package com.duoc.app_spring.service;

import com.duoc.app_spring.dto.ClienteCreateDTO;
import com.duoc.app_spring.dto.ClienteDTO;

import java.util.List;

public interface ClienteService {
    
    List<ClienteDTO> findAll();
    
    ClienteDTO findById(Long id);
    
    ClienteDTO findByRut(String rut);
    
    ClienteDTO create(ClienteCreateDTO clienteCreateDTO);
    
    ClienteDTO update(Long id, ClienteDTO clienteDTO);
    
    void delete(Long id);
    
    ClienteDTO activar(Long id);
    
    ClienteDTO desactivar(Long id);
}