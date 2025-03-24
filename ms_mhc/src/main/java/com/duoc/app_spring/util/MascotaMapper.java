package com.duoc.app_spring.util;

import com.duoc.app_spring.dto.MascotaDTO;
import com.duoc.app_spring.model.Mascota;
import org.springframework.stereotype.Component;

@Component
public class MascotaMapper {
    
    public MascotaDTO toDTO(Mascota mascota) {
        if (mascota == null) {
            return null;
        }
        
        MascotaDTO dto = new MascotaDTO();
        dto.setIdMascota(mascota.getIdMascota());
        dto.setIdCliente(mascota.getIdCliente());
        dto.setNombre(mascota.getNombre());
        dto.setEspecie(mascota.getEspecie());
        dto.setRaza(mascota.getRaza());
        dto.setFechaNacimiento(mascota.getFechaNacimiento());
        dto.setSexo(mascota.getSexo());
        dto.setColor(mascota.getColor());
        dto.setPeso(mascota.getPeso());
        dto.setActivo(mascota.getActivo());
        dto.setFechaRegistro(mascota.getFechaRegistro());
        
        return dto;
    }
    
    public Mascota toEntity(MascotaDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Mascota mascota = new Mascota();
        mascota.setIdMascota(dto.getIdMascota());
        mascota.setIdCliente(dto.getIdCliente());
        mascota.setNombre(dto.getNombre());
        mascota.setEspecie(dto.getEspecie());
        mascota.setRaza(dto.getRaza());
        mascota.setFechaNacimiento(dto.getFechaNacimiento());
        mascota.setSexo(dto.getSexo());
        mascota.setColor(dto.getColor());
        mascota.setPeso(dto.getPeso());
        mascota.setActivo(dto.getActivo());
        mascota.setFechaRegistro(dto.getFechaRegistro());
        
        return mascota;
    }
}