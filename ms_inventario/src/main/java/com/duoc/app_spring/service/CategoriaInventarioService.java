package com.duoc.app_spring.service;

import com.duoc.app_spring.dto.CategoriaInventarioDTO;
import com.duoc.app_spring.model.CategoriaInventario;
import com.duoc.app_spring.repository.CategoriaInventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriaInventarioService {

    @Autowired
    private CategoriaInventarioRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<CategoriaInventarioDTO> findAll() {
        return categoriaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<CategoriaInventarioDTO> findById(Long id) {
        return categoriaRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public List<CategoriaInventarioDTO> findByNombre(String nombre) {
        return categoriaRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoriaInventarioDTO save(CategoriaInventarioDTO categoriaDTO) {
        CategoriaInventario categoria = convertToEntity(categoriaDTO);
        categoria = categoriaRepository.save(categoria);
        return convertToDTO(categoria);
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (categoriaRepository.existsById(id)) {
            categoriaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private CategoriaInventarioDTO convertToDTO(CategoriaInventario categoria) {
        CategoriaInventarioDTO dto = new CategoriaInventarioDTO();
        dto.setIdCategoria(categoria.getIdCategoria());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        return dto;
    }

    private CategoriaInventario convertToEntity(CategoriaInventarioDTO dto) {
        CategoriaInventario categoria = new CategoriaInventario();
        categoria.setIdCategoria(dto.getIdCategoria());
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        return categoria;
    }
}