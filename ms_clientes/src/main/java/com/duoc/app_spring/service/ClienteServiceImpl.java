package com.duoc.app_spring.service;

import com.duoc.app_spring.dto.ClienteCreateDTO;
import com.duoc.app_spring.dto.ClienteDTO;
import com.duoc.app_spring.dto.NotificacionDTO;
import com.duoc.app_spring.model.Cliente;
import com.duoc.app_spring.repository.ClienteRepository;
import com.duoc.app_spring.util.ClienteMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final AzureFunctionService azureFunctionService;

    public ClienteServiceImpl(ClienteRepository clienteRepository, 
                             ClienteMapper clienteMapper,
                             AzureFunctionService azureFunctionService) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.azureFunctionService = azureFunctionService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> findAll() {
        List<Cliente> clientes = clienteRepository.findAll();
        return clientes.stream()
                .map(clienteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO findById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + id));
        return clienteMapper.toDTO(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO findByRut(String rut) {
        Cliente cliente = clienteRepository.findByRut(rut)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con RUT: " + rut));
        return clienteMapper.toDTO(cliente);
    }

    @Override
    @Transactional
    public ClienteDTO create(ClienteCreateDTO clienteCreateDTO) {
        // Verificar si ya existe un cliente con el mismo RUT
        if (clienteRepository.existsByRut(clienteCreateDTO.getRut())) {
            throw new IllegalArgumentException("Ya existe un cliente con el RUT: " + clienteCreateDTO.getRut());
        }
        
        // Crear un nuevo cliente
        Cliente cliente = new Cliente();
        cliente.setRut(clienteCreateDTO.getRut());
        cliente.setNombres(clienteCreateDTO.getNombres());
        cliente.setApellidos(clienteCreateDTO.getApellidos());
        cliente.setEmail(clienteCreateDTO.getEmail());
        cliente.setTelefono(clienteCreateDTO.getTelefono());
        cliente.setDireccion(clienteCreateDTO.getDireccion());
        cliente.setCiudad(clienteCreateDTO.getCiudad());
        cliente.setFechaRegistro(LocalDate.now());
        cliente.setActivo(true);
        
        Cliente clienteGuardado = clienteRepository.save(cliente);
        ClienteDTO clienteDTO = clienteMapper.toDTO(clienteGuardado);
        
        // Enviar notificación de cliente creado
        enviarNotificacionClienteCreado(clienteDTO);
        
        return clienteDTO;
    }

    @Override
    @Transactional
    public ClienteDTO update(Long id, ClienteDTO clienteDTO) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + id));
        
        // Si se está cambiando el RUT, verificar que no exista otro cliente con ese RUT
        if (!clienteExistente.getRut().equals(clienteDTO.getRut()) && 
                clienteRepository.existsByRut(clienteDTO.getRut())) {
            throw new IllegalArgumentException("Ya existe un cliente con el RUT: " + clienteDTO.getRut());
        }
        
        clienteExistente.setRut(clienteDTO.getRut());
        clienteExistente.setNombres(clienteDTO.getNombres());
        clienteExistente.setApellidos(clienteDTO.getApellidos());
        clienteExistente.setEmail(clienteDTO.getEmail());
        clienteExistente.setTelefono(clienteDTO.getTelefono());
        clienteExistente.setDireccion(clienteDTO.getDireccion());
        clienteExistente.setCiudad(clienteDTO.getCiudad());
        
        Cliente clienteActualizado = clienteRepository.save(clienteExistente);
        ClienteDTO clienteActualizadoDTO = clienteMapper.toDTO(clienteActualizado);
        
        // Enviar notificación de cliente actualizado
        enviarNotificacionClienteActualizado(clienteActualizadoDTO);
        
        return clienteActualizadoDTO;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new EntityNotFoundException("Cliente no encontrado con ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ClienteDTO activar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + id));
        cliente.setActivo(true);
        Cliente clienteActivado = clienteRepository.save(cliente);
        return clienteMapper.toDTO(clienteActivado);
    }

    @Override
    @Transactional
    public ClienteDTO desactivar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + id));
        cliente.setActivo(false);
        Cliente clienteDesactivado = clienteRepository.save(cliente);
        ClienteDTO clienteDTO = clienteMapper.toDTO(clienteDesactivado);
        
        // Enviar notificación de cliente desactivado
        enviarNotificacionClienteDesactivado(clienteDTO);
        
        return clienteDTO;
    }

    private void enviarNotificacionClienteCreado(ClienteDTO cliente) {
        NotificacionDTO notificacion = NotificacionDTO.builder()
                .idCliente(cliente.getIdCliente())
                .tipo("CLIENTE_CREADO")
                .titulo("Bienvenido a Veterinaria CloudNative")
                .mensaje("Estimado/a " + cliente.getNombres() + ", ¡bienvenido a nuestra veterinaria! Su cuenta ha sido creada exitosamente.")
                .fechaCreacion(LocalDateTime.now())
                .canal("email")
                .build();
        
        azureFunctionService.enviarNotificacion(notificacion);
    }

    private void enviarNotificacionClienteActualizado(ClienteDTO cliente) {
        NotificacionDTO notificacion = NotificacionDTO.builder()
                .idCliente(cliente.getIdCliente())
                .tipo("CLIENTE_ACTUALIZADO")
                .titulo("Información actualizada")
                .mensaje("Estimado/a " + cliente.getNombres() + ", su información ha sido actualizada correctamente.")
                .fechaCreacion(LocalDateTime.now())
                .canal("email")
                .build();
        
        azureFunctionService.enviarNotificacion(notificacion);
    }
    
    private void enviarNotificacionClienteDesactivado(ClienteDTO cliente) {
        NotificacionDTO notificacion = NotificacionDTO.builder()
                .idCliente(cliente.getIdCliente())
                .tipo("CLIENTE_DESACTIVADO")
                .titulo("Cuenta desactivada")
                .mensaje("Estimado/a " + cliente.getNombres() + ", su cuenta ha sido desactivada. Si desea reactivarla, por favor contáctenos.")
                .fechaCreacion(LocalDateTime.now())
                .canal("email")
                .build();
        
        azureFunctionService.enviarNotificacion(notificacion);
    }
}