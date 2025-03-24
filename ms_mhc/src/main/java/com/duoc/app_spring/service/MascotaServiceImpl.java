package com.duoc.app_spring.service;

import com.duoc.app_spring.dto.MascotaCreateDTO;
import com.duoc.app_spring.dto.MascotaDTO;
import com.duoc.app_spring.model.Mascota;
import com.duoc.app_spring.repository.MascotaRepository;
import com.duoc.app_spring.util.MascotaMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MascotaServiceImpl implements MascotaService {

    private final MascotaRepository mascotaRepository;
    private final MascotaMapper mascotaMapper;

    public MascotaServiceImpl(MascotaRepository mascotaRepository, MascotaMapper mascotaMapper) {
        this.mascotaRepository = mascotaRepository;
        this.mascotaMapper = mascotaMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaDTO> findAll() {
        List<Mascota> mascotas = mascotaRepository.findAll();
        return mascotas.stream()
                .map(mascotaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MascotaDTO findById(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mascota no encontrada con ID: " + id));
        return mascotaMapper.toDTO(mascota);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaDTO> findByCliente(Long idCliente) {
        List<Mascota> mascotas = mascotaRepository.findByIdCliente(idCliente);
        return mascotas.stream()
                .map(mascotaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaDTO> findActivasByCliente(Long idCliente) {
        List<Mascota> mascotas = mascotaRepository.findByIdClienteAndActivoTrue(idCliente);
        return mascotas.stream()
                .map(mascotaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MascotaDTO create(MascotaCreateDTO mascotaCreateDTO) {
        Mascota mascota = new Mascota();
        mascota.setIdCliente(mascotaCreateDTO.getIdCliente());
        mascota.setNombre(mascotaCreateDTO.getNombre());
        mascota.setEspecie(mascotaCreateDTO.getEspecie());
        mascota.setRaza(mascotaCreateDTO.getRaza());
        mascota.setFechaNacimiento(mascotaCreateDTO.getFechaNacimiento());
        mascota.setSexo(mascotaCreateDTO.getSexo());
        mascota.setColor(mascotaCreateDTO.getColor());
        mascota.setPeso(mascotaCreateDTO.getPeso());
        mascota.setActivo(true);
        mascota.setFechaRegistro(LocalDate.now());
        
        Mascota mascotaGuardada = mascotaRepository.save(mascota);
        return mascotaMapper.toDTO(mascotaGuardada);
    }

    @Override
    @Transactional
    public MascotaDTO update(Long id, MascotaDTO mascotaDTO) {
        Mascota mascotaExistente = mascotaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mascota no encontrada con ID: " + id));
        
        mascotaExistente.setIdCliente(mascotaDTO.getIdCliente());
        mascotaExistente.setNombre(mascotaDTO.getNombre());
        mascotaExistente.setEspecie(mascotaDTO.getEspecie());
        mascotaExistente.setRaza(mascotaDTO.getRaza());
        mascotaExistente.setFechaNacimiento(mascotaDTO.getFechaNacimiento());
        mascotaExistente.setSexo(mascotaDTO.getSexo());
        mascotaExistente.setColor(mascotaDTO.getColor());
        mascotaExistente.setPeso(mascotaDTO.getPeso());
        
        Mascota mascotaActualizada = mascotaRepository.save(mascotaExistente);
        return mascotaMapper.toDTO(mascotaActualizada);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!mascotaRepository.existsById(id)) {
            throw new EntityNotFoundException("Mascota no encontrada con ID: " + id);
        }
        mascotaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public MascotaDTO activar(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mascota no encontrada con ID: " + id));
        mascota.setActivo(true);
        Mascota mascotaActivada = mascotaRepository.save(mascota);
        return mascotaMapper.toDTO(mascotaActivada);
    }

    @Override
    @Transactional
    public MascotaDTO desactivar(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mascota no encontrada con ID: " + id));
        mascota.setActivo(false);
        Mascota mascotaDesactivada = mascotaRepository.save(mascota);
        return mascotaMapper.toDTO(mascotaDesactivada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaDTO> findByEspecie(String especie) {
        List<Mascota> mascotas = mascotaRepository.findByEspecieIgnoreCase(especie);
        return mascotas.stream()
                .map(mascotaMapper::toDTO)
                .collect(Collectors.toList());
    }
}