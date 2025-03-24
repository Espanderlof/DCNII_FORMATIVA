package com.duoc.app_spring.service;

import com.duoc.app_spring.dto.MascotaCreateDTO;
import com.duoc.app_spring.dto.MascotaDTO;

import java.util.List;

public interface MascotaService {
    
    List<MascotaDTO> findAll();
    
    MascotaDTO findById(Long id);
    
    List<MascotaDTO> findByCliente(Long idCliente);
    
    List<MascotaDTO> findActivasByCliente(Long idCliente);
    
    MascotaDTO create(MascotaCreateDTO mascotaCreateDTO);
    
    MascotaDTO update(Long id, MascotaDTO mascotaDTO);
    
    void delete(Long id);
    
    MascotaDTO activar(Long id);
    
    MascotaDTO desactivar(Long id);
    
    List<MascotaDTO> findByEspecie(String especie);
}